package common.utils.auth.rest;

import common.utils.auth.annos.AnonymousPostMapping;
import common.utils.auth.entity.dto.JwtUserDto;
import common.utils.auth.entity.dto.req.AuthUserReq;
import common.utils.auth.security.RsaProperties;
import common.utils.auth.security.TokenProvider;
import common.utils.auth.security.bean.LoginProperties;
import common.utils.auth.security.bean.SecurityProperties;
import common.utils.auth.service.impl.OnlineUserService;
import common.utils.auth.utils.RedisUtils;
import common.utils.auth.utils.RsaUtils;
import common.utils.auth.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fengxi
 * @className UserController
 * @description
 * @date 2022年12月02日 14:57
 */
@RestController
@RequestMapping("/user")
@Api(value = "用户认证", tags = "网关认证")
@Slf4j
public class UserController {

    @Resource
    private LoginProperties loginProperties;
    @Autowired
    private SecurityProperties properties;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private OnlineUserService onlineUserService;

    @ApiOperation("登录授权")
    @AnonymousPostMapping(value = "/login")
    public Object login(@Validated @RequestBody AuthUserReq authUser, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String sessionId = null;//dorado 的session id
        log.info("authUser is {}",authUser);
        // 查询验证码
        String code = (String) redisUtils.get(authUser.getUuid());
        // 清除验证码
        redisUtils.del(authUser.getUuid());
        if (StringUtils.isBlank(code)) {
            throw new RuntimeException("验证码不存在或已过期");
        }
        if (StringUtils.isBlank(authUser.getCode()) || !authUser.getCode().equalsIgnoreCase(code)) {
            throw new RuntimeException("验证码错误");
        }
        // 密码解密
        String password = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey, authUser.getPassword());
        log.info(authUser.getUsername() +":--------登录-------------:"+ password );


        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authUser.getUsername(), password);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 生成令牌与第三方系统获取令牌方式
        // UserDetails userDetails = userDetailsService.loadUserByUsername(userInfo.getUsername());
        // Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // SecurityContextHolder.getContext().setAuthentication(authentication);
        //no-use start
        String token = tokenProvider.createToken(authentication);
        final JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();
        // 保存在线信息
        onlineUserService.save(jwtUserDto, token, request);

        // 返回 token 与 用户信息
        String finalSessionId = sessionId;
        Map<String, Object> authInfo = new HashMap<String, Object>(5) {{
            put("token", properties.getTokenStartWith() + token);
            put("user", jwtUserDto);
            put("userType", 4);
//            put("sessionId", finalSessionId);
        }};

        if (loginProperties.isSingleLogin()) {
            //踢掉之前已经登录的token
            onlineUserService.checkLoginOnUser(authUser.getUsername(), token);
        }
        return authInfo;

    }
}
