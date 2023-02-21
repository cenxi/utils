package common.utils.designpattern.chain.impl;

import com.alibaba.fastjson.JSON;
import common.utils.designpattern.chain.BaseAlertHandler;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 报警回调处理器
 */
@Slf4j
@Service
public class CallbackHandler extends BaseAlertHandler {

    public CallbackHandler(Config configs) {
        super(configs);
    }

    @Override
    public boolean handle(AlertMsg msg) {
        // do
        return true;
    }

}
