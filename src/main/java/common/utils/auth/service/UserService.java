package common.utils.auth.service;

import common.utils.auth.entity.dto.UserDto;
import common.utils.auth.entity.model.CcSysUser;

/**
 * @author fengxi
 * @className UserService
 * @description
 * @date 2022年12月02日 14:28
 */
public interface UserService {

    UserDto findByName(String name);
}
