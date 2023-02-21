package common.utils.designpattern.chain.impl;

import common.utils.designpattern.chain.BaseAlertHandler;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 报警页面消息发布处理器
 */
@Slf4j
@Component
public class AlertPushHandler extends BaseAlertHandler {

    public AlertPushHandler(Config configs) {
        super(configs);
    }

    @Override
    public boolean handle(AlertMsg msg) {
        // handle
        return true;
    }


}
