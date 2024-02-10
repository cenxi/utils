package common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author fengxi
 * @className App
 * @description
 * @date 2023年01月06日 13:53
 */
@Slf4j
@SpringBootApplication
public class App {

    public static void main(String[] args) {
        log.info("system start");
        SpringApplication.run(App.class, args);
    }
}
