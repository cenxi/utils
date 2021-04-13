package common.utils.response;

import lombok.Data;

/**
 * @author :fengxi
 * @date :Created in 2021/4/13 5:48 下午
 * @description：
 * @modified By:
 */
@Data
public class Response<T> {

    private Integer code;

    private String msg;

    private T data;
}
