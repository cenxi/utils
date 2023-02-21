package common.utils.designpattern.chain;

import common.utils.designpattern.chain.entity.AlertMsg;

/**
 * 报警消息驱动器
 *  消息生产者
 */
public class BaseAlertEngine {

    protected final AlertHandlerChain handlerChain;

    protected final Object configs;

    public BaseAlertEngine(AlertHandlerChain handlerChain, Object configs) {
        this.handlerChain = handlerChain;
        this.configs = configs;
    }

    /**
     * 执行报警消息处理逻辑
     * @param msg 报警消息
     */
    protected void doHandle(AlertMsg msg){
        handlerChain.handle(msg);
    }
}
