package common.utils.designpattern.chain.impl;

import common.utils.designpattern.chain.AlertHandlerChain;
import common.utils.designpattern.chain.BaseAlertEngine;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
import jodd.util.concurrent.ThreadFactoryBuilder;
import org.springframework.boot.CommandLineRunner;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * mock报警数据驱动
 */
//@Component
public class MockAlertEngine extends BaseAlertEngine implements CommandLineRunner {

    private final long delay = 30000;

    private final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
            1,
            new ThreadFactoryBuilder().setNameFormat("Alert-Mock").get(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public MockAlertEngine(AlertHandlerChain handlerChain, Config configs) {
        super(handlerChain, configs);
    }

    /**
     * 启动数据mock定时任务
     * @param args 入参
     * @throws Exception 异常
     */
    @Override
    public void run(String... args) throws Exception {
        executor.scheduleWithFixedDelay(() -> doHandle(mock()), delay, delay, TimeUnit.MILLISECONDS);
    }

    private final Random rd = new Random();

    private AlertMsg mock(){
        int rid = rd.nextInt(10);
        LocalDateTime now = LocalDateTime.now();
        AlertMsg msg = new AlertMsg();
        msg.setModelId("348548717630984192");
        msg.setDeviceId("347041283871608832");
        msg.setAlertTime(now);
        msg.setSrcImage("http://101.43.31.4/upload/vod/20211121-1/pic.png");
        msg.setAnalyzedImage("http://101.43.31.4/upload/vod/20211121-1/pic.png");
        return msg;
    }

}
