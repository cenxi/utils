package common.utils.auth.entity.model;

import common.utils.auth.entity.dto.UserDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.Objects;

/**
 * @author zb
 * @date 2019-11-22
 */
@Getter
@Setter
public class CcSysUser {

    @ApiModelProperty(value = "ID", hidden = true)
    private Long user_id;

    @ApiModelProperty(value = "部门id")
    private Long deptId;

    @ApiModelProperty(value = "用户名称")
    private String username;

    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "电话号码")
    private String phone;

    @ApiModelProperty(value = "用户性别")
    private String gender;

    @ApiModelProperty(value = "头像真实名称",hidden = true)
    private String avatarName;

    @ApiModelProperty(value = "头像存储的路径", hidden = true)
    private String avatarPath;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "是否为admin账号", hidden = true)
    private Boolean isAdmin = false;

    @ApiModelProperty(value = "最后修改密码的时间", hidden = true)
    private Date pwdResetTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CcSysUser user = (CcSysUser) o;
        return Objects.equals(user_id, user.user_id) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user_id, username);
    }

}
