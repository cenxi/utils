package common.utils.response;

/**
 * 通用错误码定义.
 */
public enum BizExceptionEnum implements BizExceptionBuilder {

    PARAM_ERROR(1000001,"传参错误"),
    SERVICE_ERROR(1000002, "服务异常"),
    DB_ERROR(1000003, "数据库异常"),



	;

	private int code;
	private String msg;

	BizExceptionEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	@Override
	public int getCode() {
		return code;
	}

	@Override
	public String getMsg() {
		return msg;
	}
}
