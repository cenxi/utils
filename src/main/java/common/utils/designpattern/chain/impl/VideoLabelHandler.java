package common.utils.designpattern.chain.impl;

import cn.hutool.core.collection.CollUtil;
import common.utils.designpattern.chain.BaseAlertHandler;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * 直播标签消息发布处理器
 */
@Slf4j
@Component
public class VideoLabelHandler extends BaseAlertHandler {


    public VideoLabelHandler(Config configs) {
        super(configs);
    }

    @Override
    public boolean handle(AlertMsg msg) {
        videoLabel(msg);
        return true;
    }

    /**
     * 推送视频标签订阅
     * @param msg 报警消息
     */
    private void videoLabel(AlertMsg msg){

    }
    
}
