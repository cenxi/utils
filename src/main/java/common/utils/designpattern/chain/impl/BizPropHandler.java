package common.utils.designpattern.chain.impl;

import common.utils.designpattern.chain.BaseAlertHandler;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
import org.springframework.stereotype.Component;

/**
 * 报警数据业务属性装载
 */
@Component
public class BizPropHandler extends BaseAlertHandler {

    public BizPropHandler(Config configs) {
        super(configs);
    }

    @Override
    public boolean handle(AlertMsg msg) {
        // handle
        return true;
    }
}
