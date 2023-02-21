package common.utils.designpattern.chain.config;

import common.utils.designpattern.chain.AlertHandlerChain;
import common.utils.designpattern.chain.entity.Config;
import common.utils.designpattern.chain.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlertHandlerConfig {

    @Bean
    public Config config(){
        return new Config();
    }

    /**
     * alertHandlerChain 实例
     * @param bizPropHandler 报警数据业务属性装配处理器
     * @param dbStoreHandler 报警数据存储处理器
     * @param videoHandler 报警视频录像处理器
     * @param unrepeatableHandler 报警去重处理器
     * @param alertPushHandler 报警页面推送处理器
     * @param videoLabelHandler 直播视频标签处理器(rect信息)
     * @param callbackHandler 报警回调处理器
     * @return AlertHandlerChain
     */
    @Bean
    public AlertHandlerChain alertHandlerChain(
            BizPropHandler bizPropHandler,
            DbStoreHandler dbStoreHandler,
            VideoHandler videoHandler,
            UnrepeatableHandler unrepeatableHandler,
            AlertPushHandler alertPushHandler,
            VideoLabelHandler videoLabelHandler,
            CallbackHandler callbackHandler
    ){
        return AlertHandlerChain.builder()
                .next(bizPropHandler)
                .next(videoLabelHandler)
                .next(unrepeatableHandler)
                .next(dbStoreHandler)
//                .next(videoHandler)
                .next(alertPushHandler)
                .next(callbackHandler)
                .build();
    }

    /**
     * kafkaAlertEngine 实例
     * @param alertHandlerChain 处理器链
     * @param configs 报警配置
     * @return KafkaAlertEngine
     */
    @Bean
    public KafkaAlertEngine kafkaAlertEngine(AlertHandlerChain alertHandlerChain, Config configs){
        return new KafkaAlertEngine(alertHandlerChain, configs);
    }

}
