package common.utils.msgqueue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.lang.reflect.Method;

/**
 * @author fengxi
 * @className ListenerMethod
 * @description
 * @date 2025年11月28日 15:19
 */
@AllArgsConstructor
@Data
@Accessors(chain = true)
public class MsgListenerInfo {

    private Object bean;

    private Method method;

    private String group;

    private int concurrency;

    private boolean topicNameWithNacosSpace;

    private boolean batch;

    private int batchSize;
}
