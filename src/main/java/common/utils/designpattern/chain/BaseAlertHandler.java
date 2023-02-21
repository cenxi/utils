package common.utils.designpattern.chain;

import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;

/**
 * 报警消息处理器基类
 */
public abstract class BaseAlertHandler {

    protected final Object configs;

    protected BaseAlertHandler next;

    protected BaseAlertHandler(Config configs) {
        this.configs = configs;
    }

    /**
     * 设置下一步的handler
     * @param next nextHandler
     * @return next BaseAlertHandler
     */
    public BaseAlertHandler next(BaseAlertHandler next){
        this.next = next;
        return next;
    }

    /**
     * 执行报警消息处理逻辑
     * @param msg 报警消息
     * @return boolean
     */
    public abstract boolean handle(AlertMsg msg);

}
