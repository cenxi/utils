package common.utils.designpattern.chain.impl;

import common.utils.designpattern.chain.AlertHandlerChain;
import common.utils.designpattern.chain.BaseAlertEngine;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;


public class KafkaAlertEngine extends BaseAlertEngine implements CommandLineRunner {


    @Autowired(required = false)
    private KafkaConsumer consumer;

    public KafkaAlertEngine(AlertHandlerChain handlerChain, Config configs) {
        super(handlerChain, configs);
    }

    /**
     * 添加报警监控
     *
     * @param topic 主题
     */
    public void addAlertListener(String topic) {
        try {
            ConsumerRecords records = consumer.poll(Duration.ofMillis(10000L));
            records.forEach(r->{
                AlertMsg alertMsg = toAlertMsg(r);
                CompletableFuture.runAsync(()->doHandle(alertMsg));
            });
        } catch (Exception e) {

        }
    }

    private AlertMsg toAlertMsg(Object r) {
        return new AlertMsg();
    }


    /**
     * 动态监听topic
     * @param args 入参
     */
    @Override
    public void run(String... args) {

        String alertTopic = "topic-1";

        // key对应topic
        addAlertListener(alertTopic);
    }
}
