package common.utils.auth.entity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public interface  UserDto {

    String getUsername();

    @JsonIgnore
    @JSONField(serialize = false)
    String getPassword();

    Boolean getEnabled();

}
