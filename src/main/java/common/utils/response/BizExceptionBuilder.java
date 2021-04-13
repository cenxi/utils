package common.utils.response;

public interface BizExceptionBuilder {


    /**
     * 错误码
     *
     */
    int getCode();

    /**
     * 错误详情
     * @return
     */
    String getMsg();

    /**
     *
     * @return BizException
     */
    default BizException exception() {
        return new BizException(getCode(),getMsg());
    }

    /**
     *
     * @return BizException
     */
    default BizException exception(Exception e) {
        return new BizException(getCode(),getMsg());
    }

}
