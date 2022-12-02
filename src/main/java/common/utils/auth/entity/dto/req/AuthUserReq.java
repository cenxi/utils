package common.utils.auth.entity.dto.req;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * @author zb
 * @date 2019-11-30
 */
@Getter
@Setter
public class AuthUserReq {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String code;

    private String uuid = "";
}
