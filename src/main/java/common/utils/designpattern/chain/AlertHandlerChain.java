package common.utils.designpattern.chain;

import common.utils.designpattern.chain.entity.AlertMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

/**
 * 报警消息处理器执行链
 */
@Slf4j
public class AlertHandlerChain {

    private final BaseAlertHandler head;

    private final boolean handlerErrorCatch;

    public AlertHandlerChain(BaseAlertHandler head, boolean handlerErrorCatch) {
        this.head = head;
        this.handlerErrorCatch = handlerErrorCatch;
    }

    /**
     * 消息处理器执行链的触发入口
     * 由消息引擎AlertEngine调用
     * @param msg 报警消息
     */
    public void handle(AlertMsg msg){
        long receivedTime = System.currentTimeMillis();
        BaseAlertHandler next = head;
        boolean result = true;
        StopWatch watch = new StopWatch("AlertHandler");
        while (result && null != next){
            Exception exception = null;
            watch.start(next.getClass().getSimpleName());
            try {
                result = next.handle(msg);
            } catch (Exception e) {
                exception = e;
                if (handlerErrorCatch){
                    continue;
                }
            } finally {
                watch.stop();
                next = next.next;
                if (null == exception){
                    log.debug("AlertHandler {} execute complete, result={}, cost {}ms", watch.getLastTaskName(), result, watch.getLastTaskTimeMillis());
                } else {
                    log.error("AlertHandler {} execute error, cost {}ms", watch.getLastTaskName(), watch.getLastTaskTimeMillis(), exception);
                }
            }
        }
    }

    /**
     * 构造Builder
     * @return Builder
     */
    public static Builder builder(){
        return new Builder();
    }

    /**
     * 构造器
     */
    public static class Builder{

        private boolean handlerErrorCatch;

        private BaseAlertHandler head;

        private BaseAlertHandler tail;

        /**
         * 指定下一个handler
         * @param handler 下一个handler
         * @return Builder
         */
        public Builder next(BaseAlertHandler handler){
            if (null == head){
                head = handler;
            }
            if (null == tail){
                tail = handler;
            } else {
                tail = tail.next(handler);
            }
            return this;
        }

        /**
         * 是否捕获每个handler的异常(handler的异常互不影响)
         * @param handlerErrorCatch 是否捕获每个handler的异常
         * @return Builder
         */
        public Builder handlerErrorCatch(boolean handlerErrorCatch){
            this.handlerErrorCatch = handlerErrorCatch;
            return this;
        }

        /**
         * 结束构造输出目标对象
         * @return AlertHandlerChain
         */
        public AlertHandlerChain build(){
            return new AlertHandlerChain(head, handlerErrorCatch);
        }

    }

}
