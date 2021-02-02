package common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.env.OriginTrackedMapPropertySource;
import org.springframework.core.env.*;
import org.springframework.stereotype.Component;

/**
 * @author :fengxi
 * @description：打印所有环境变量(便于系统启动后，错误环境变量排查)
 * @modified By:
 */
@Component
@Slf4j
public class EnvLogHandler implements CommandLineRunner {

    @Autowired
    private Environment env;
    @Override
    public void run(String... args) throws Exception {
        MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
        for (PropertySource<?> source : sources) {
            if (source instanceof OriginTrackedMapPropertySource) {
                OriginTrackedMapPropertySource propertySource = (OriginTrackedMapPropertySource) source;
                log.info("###################系统环境变量###################");
                for (String s : propertySource.getPropertyNames()) {
                    log.info("{}-->{}", s, env.getProperty(s));
                }
                log.info("###################系统环境变量###################");
            }
        }
    }
}
