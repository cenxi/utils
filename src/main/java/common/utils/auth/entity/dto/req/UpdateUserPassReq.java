package common.utils.auth.entity.dto.req;

import lombok.Data;

/**
 * 修改密码的 Vo 类
 * @author zb
 * @date 2019年7月11日13:59:49
 */
@Data
public class UpdateUserPassReq {

    private String oldPass;

    private String newPass;
}
