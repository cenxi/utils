package common.utils.auth.entity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author zb
 * @date 2019-11-23
 */
@Getter
@Setter
public class UserDto {

    private Long id;

    private Long deptId;

    private String username;

    private String nickName;

    private String email;

    private String phone;

    private String gender;

    private String avatarName;

    private String avatarPath;

    @JsonIgnore
    @JSONField(serialize = false)
    private String password;

    private Boolean enabled;

    @JsonIgnore
    @JSONField(serialize = false)
    private Boolean isAdmin = false;

    private Date pwdResetTime;
}
