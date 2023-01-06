package common.utils.auth.service.impl;

import common.utils.auth.entity.dto.UserDto;
import common.utils.auth.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author fengxi
 * @className UserServiceImpl
 * @description
 * @date 2023年01月06日 14:01
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 扩展方法，自定义查数据库
     * @param name
     * @return
     */
    @Override
    public UserDto findByName(String name) {
        return null;
    }

    /**
     * 扩展方法，自定义查数据库
     * @param username
     * @param pwd
     */
    @Override
    public void updatePass(String username, String pwd) {

    }
}
