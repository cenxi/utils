package common.utils.msgqueue.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author fengxi
 * @className TestLogHandler
 * @description
 * @date 2025年11月26日 10:35
 */
@Component
@Slf4j
public class TestLogHandler {

    public void printLog(String msg) {
        log.info("打印日志->{}", msg);
    }
}
