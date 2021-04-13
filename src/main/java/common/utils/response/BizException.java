package common.utils.response;


import lombok.Data;

@Data
public class BizException{

    private Integer code;
    private String msg;

    public BizException(Integer code, String msg) {
        this.code=code;
        this.msg = msg;
    }

}
