package common.utils.designpattern.chain.impl;

import common.utils.designpattern.chain.BaseAlertHandler;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
import org.springframework.stereotype.Component;

/**
 * 报警数据存储处理器
 */
@Component
public class DbStoreHandler extends BaseAlertHandler {


    public DbStoreHandler(Config configs) {
        super(configs);
    }

    @Override
    public boolean handle(AlertMsg msg) {

        // handle
        return true;
    }
}
