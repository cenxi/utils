package common.utils.auth.service.impl;

import common.utils.auth.entity.dto.JwtUserDto;
import common.utils.auth.entity.dto.UserDto;
import common.utils.auth.security.bean.LoginProperties;
import common.utils.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;
    private final LoginProperties loginProperties;

    public void setEnableCache(boolean enableCache) {
        this.loginProperties.setCacheEnable(enableCache);
    }

    /**
     * 用户信息缓存
     *
     * @see {@link UserCacheClean}
     */
    static Map<String, JwtUserDto> userDtoCache = new ConcurrentHashMap<>();

    @Override
    public JwtUserDto loadUserByUsername(String username) {
        boolean searchDb = true;
        JwtUserDto jwtUserDto = null;
        if (loginProperties.isCacheEnable() && userDtoCache.containsKey(username)) {
            jwtUserDto = userDtoCache.get(username);
            // 检查dataScope是否修改
            List<Long> dataScopes = jwtUserDto.getDataScopes();
            dataScopes.clear();
            searchDb = false;
        }
        if (searchDb) {
            UserDto user = userService.findByName(username);
            if (user == null) {
                throw new UsernameNotFoundException("");
            } else {
                if (user == null) {
                    throw new UsernameNotFoundException("");
                } else {
                    if (!user.getEnabled()) {
                        throw new RuntimeException("账号未激活！");
                    }
                    jwtUserDto = new JwtUserDto(
                            user,
                            null
                    );
                    userDtoCache.put(username, jwtUserDto);
                }
            }
        }
        return jwtUserDto;
    }
}
