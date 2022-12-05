package common.utils.auth.rest;

import cn.hutool.core.util.IdUtil;
import com.wf.captcha.base.Captcha;
import common.utils.auth.annos.AnonymousDeleteMapping;
import common.utils.auth.annos.AnonymousGetMapping;
import common.utils.auth.annos.AnonymousPostMapping;
import common.utils.auth.entity.dto.JwtUserDto;
import common.utils.auth.entity.dto.UserDto;
import common.utils.auth.entity.dto.req.AuthUserReq;
import common.utils.auth.entity.dto.req.UpdateUserPassReq;
import common.utils.auth.security.RsaProperties;
import common.utils.auth.security.TokenProvider;
import common.utils.auth.security.bean.LoginCodeEnum;
import common.utils.auth.security.bean.LoginProperties;
import common.utils.auth.security.bean.SecurityProperties;
import common.utils.auth.service.UserService;
import common.utils.auth.service.impl.OnlineUserService;
import common.utils.auth.utils.RedisUtils;
import common.utils.auth.utils.RsaUtils;
import common.utils.auth.utils.SecurityUtils;
import common.utils.auth.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private PasswordEncoder passwordEncoder;
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
    @Autowired
    private UserService userService;

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

    @ApiOperation("获取用户信息")
    @GetMapping(value = "/info")
    public Object getUserInfo() {
        return SecurityUtils.getCurrentUser();
    }

    @ApiOperation("退出登录")
    @AnonymousDeleteMapping(value = "/logout")
    public Object logout(HttpServletRequest request) {
        onlineUserService.logout(tokenProvider.getToken(request));
        return null;
    }

    @ApiOperation("获取验证码")
    @AnonymousGetMapping(value = "/code")
    public Object getCode() {
        // 获取运算的结果
        Captcha captcha = loginProperties.getCaptcha();
        String uuid = properties.getCodeKey() + IdUtil.simpleUUID();
        //当验证码类型为 arithmetic时且长度 >= 2 时，captcha.text()的结果有几率为浮点型
        String captchaValue = captcha.text();
        if (captcha.getCharType() - 1 == LoginCodeEnum.arithmetic.ordinal() && captchaValue.contains(".")) {
            captchaValue = captchaValue.split("\\.")[0];
        }
        // 保存
        redisUtils.set(uuid, captchaValue, loginProperties.getLoginCode().getExpiration(), TimeUnit.MINUTES);
        // 验证码信息
        Map<String, Object> imgResult = new HashMap<String, Object>(2) {{
            put("img", captcha.toBase64());
            put("uuid", uuid);
        }};
        return imgResult;
    }

    @ApiOperation("修改密码")
    @PostMapping(value = "/updatePass")
    public Object updatePass(@RequestBody UpdateUserPassReq userPassReq) throws Exception {

        String oldPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,userPassReq.getOldPass());
        String newPass = RsaUtils.decryptByPrivateKey(RsaProperties.privateKey,userPassReq.getNewPass());
        UserDto user = userService.findByName(SecurityUtils.getCurrentUsername());
        if(!passwordEncoder.matches(oldPass, user.getPassword())){
            throw new RuntimeException("修改失败，旧密码错误");
        }
        if(passwordEncoder.matches(newPass, user.getPassword())){
            throw new RuntimeException("新密码不能与旧密码相同");
        }

        userService.updatePass(user.getUsername(),passwordEncoder.encode(newPass));

        return null;
    }
}
