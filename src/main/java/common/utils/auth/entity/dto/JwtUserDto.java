package common.utils.auth.entity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * @author zb
 * @date 2019-11-23
 */
@Getter
@AllArgsConstructor
public class JwtUserDto implements UserDetails {

    private final UserDto user;

    private final List<Long> dataScopes;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public String getUsername() {
        return user.getUsername();
    }

    @JSONField(serialize = false)
    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JSONField(serialize = false)
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JSONField(serialize = false)
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    @JsonIgnore
    public boolean isEnabled() {
        return user.getEnabled();
    }
}
