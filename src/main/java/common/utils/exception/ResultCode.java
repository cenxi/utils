package common.utils.exception;

/**
 * @author fengxi
 * @date 2022年09月13日 10:55
 */
public enum ResultCode implements IErrorCode {
    SUCCESS(200L, "操作成功"),
    SAMEDATA(201L, "数据重复"),
    FAILED(500L, "操作失败"),
    VALIDATE_FAILED(404L, "参数检验失败"),
    UNAUTHORIZED(401L, "暂未登录或token已经过期"),
    FORBIDDEN(403L, "没有相关权限");

    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public long getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
