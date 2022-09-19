package common.utils.exception;

/**
 * @author fengxi
 * @date 2022年09月13日 10:54
 */
public class CommonResult<T> {
    private long code;
    private String message;
    private T data;

    public CommonResult() {
    }

    public CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> CommonResult<T> success(T data) {
        return new CommonResult(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> CommonResult<T> sameData(T data, String message) {
        return new CommonResult(ResultCode.SAMEDATA.getCode(), message, data);
    }

    public static <T> CommonResult<T> sameData(T data) {
        return new CommonResult(ResultCode.SAMEDATA.getCode(), ResultCode.SAMEDATA.getMessage(), data);
    }

    public static <T> CommonResult<T> failed(IErrorCode errorCode) {
        return new CommonResult(errorCode.getCode(), errorCode.getMessage(), (Object)null);
    }

    public static <T> CommonResult<T> failed(IErrorCode errorCode, String message) {
        return new CommonResult(errorCode.getCode(), message, (Object)null);
    }

    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult(ResultCode.FAILED.getCode(), message, (Object)null);
    }

    public static <T> CommonResult<T> failed() {
        return failed((IErrorCode)ResultCode.FAILED);
    }

    public static <T> CommonResult<T> validateFailed() {
        return failed((IErrorCode)ResultCode.VALIDATE_FAILED);
    }

    public static <T> CommonResult<T> validateFailed(String message) {
        return new CommonResult(ResultCode.VALIDATE_FAILED.getCode(), message, (Object)null);
    }

    public static <T> CommonResult<T> unauthorized(T data) {
        return new CommonResult(ResultCode.UNAUTHORIZED.getCode(), ResultCode.UNAUTHORIZED.getMessage(), data);
    }

    public static <T> CommonResult<T> forbidden(T data) {
        return new CommonResult(ResultCode.FORBIDDEN.getCode(), ResultCode.FORBIDDEN.getMessage(), data);
    }

    public long getCode() {
        return this.code;
    }

    public void setCode(long code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
