package common.utils.designpattern.chain.impl;

import common.utils.designpattern.chain.BaseAlertHandler;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
import org.springframework.stereotype.Component;

/**
 * 报警视频处理器
 * todo
 */
@Component
public class VideoHandler extends BaseAlertHandler {

    public VideoHandler(Config configs) {
        super(configs);
    }

    @Override
    public boolean handle(AlertMsg msg) {
        return true;
    }
}
