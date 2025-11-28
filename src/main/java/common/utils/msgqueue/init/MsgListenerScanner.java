package common.utils.msgqueue.init;

import cn.hutool.core.util.StrUtil;
import common.utils.msgqueue.anno.MsgListener;
import common.utils.msgqueue.consumer.MsgConsumer;
import common.utils.msgqueue.dto.MsgListenerInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MsgListenerScanner implements BeanPostProcessor, SmartInitializingSingleton {

    // 存储所有带 @MsgListener 注解的方法信息
    private final Map<String, List<MsgListenerInfo>> listenerMethodsMap = new ConcurrentHashMap<>();

    // 存储每个消费者组的线程池
    private final Map<String, ExecutorService> groupExecutorMap = new ConcurrentHashMap<>();

    @Autowired
    private MsgConsumer msgConsumer;

    @Value("${spring.cloud.nacos.config.namespace}")
    String namespace;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 检查当前 Bean 是否包含 @MsgListener 注解
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(MsgListener.class)) {
                MsgListener msgListener = method.getAnnotation(MsgListener.class);
                String topic = msgListener.topic();
                String group = msgListener.group();
                int concurrency = msgListener.concurrency();
                boolean topicNameWithNacosSpace = msgListener.topicNameWithNacosSpace();

                if (topicNameWithNacosSpace) {
                    topic = topic + StrUtil.C_UNDERLINE + namespace;
                }
                // 将方法和 Bean 实例封装为 ListenerMethod 对象
                listenerMethodsMap.computeIfAbsent(topic, k -> new ArrayList<>())
                        .add(new MsgListenerInfo(bean, method, group, concurrency, topicNameWithNacosSpace));

                log.info("发现 @MsgListener 方法: {}, topic->{}, group->{}, concurrency->{}",
                        method.getName(), topic, group, concurrency);
            }
        }
        return bean;
    }

    @Override
    public void afterSingletonsInstantiated() {
        // 启动消费者
        startConsumers();
    }

    private void startConsumers() {

        if (listenerMethodsMap.isEmpty()) {
            log.warn("未找到任何 @MsgListener 注解的方法");
            return;
        }

        log.info("开始扫描注册MsgListener");

        // 遍历所有主题和消费者组，启动消费者
        for (String topic : listenerMethodsMap.keySet()) {
            List<MsgListenerInfo> msgListenerInfos = listenerMethodsMap.get(topic);
            Set<String> groups = msgListenerInfos.stream()
                    .map(MsgListenerInfo::getGroup)
                    .collect(Collectors.toSet());

            // 如果是 MemMsgConsumer，则注册消费者组
            msgConsumer.registerGroups(topic, groups.toArray(new String[0]));

            // 启动消费
            for (MsgListenerInfo msgListenerInfo : msgListenerInfos) {
                String group = msgListenerInfo.getGroup();
                int concurrency = msgListenerInfo.getConcurrency();

                // 为每个消费者组创建固定大小的线程池
                groupExecutorMap.computeIfAbsent(group, k -> Executors.newFixedThreadPool(concurrency));

                // 提交消费任务到线程池
                for (int i = 0; i < concurrency; i++) {
                    groupExecutorMap.get(group).submit(() -> {
                        msgConsumer.consume(topic, group, msg -> {
                            try {
                                log.info("调用监听方法处理消息, topic->{}, group->{}, concurrency->{}",
                                        topic, group, concurrency);
                                msgListenerInfo.getMethod().invoke(msgListenerInfo.getBean(), msg);
                            } catch (Exception e) {
                                log.error("消息处理失败", e);
                            }
                        });
                    });
                }
            }
        }
    }
}