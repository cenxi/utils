package hbkjg.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author fengxi
 * @className ContactResp
 * @description
 * @date 2023年07月13日 14:32
 */
@Data
public class ContactResp {

    @JsonProperty("fid")
    private String fid;

    @JsonProperty("fcontactCode")
    private String fcontactCode;

    @JsonProperty("fuid")
    private String fuid;

    @JsonProperty("fname")
    private String fname;

    @JsonProperty("fidNoType")
    private String fidNoType;

    @JsonProperty("fidNo")
    private String fidNo;

    @JsonProperty("fmobile")
    private String fmobile;

    @JsonProperty("fcreateTime")
    private String fcreateTime;
    @JsonProperty("fmodifyTime")
    private String fmodifyTime;
    @JsonProperty("fstatus")
    private Integer fstatus;
    @JsonProperty("fsign")
    private String fsign;
    @JsonProperty("frealName")
    private Integer frealName;
    @JsonProperty("frequentContact")
    private String frequentContact;
    @JsonProperty("positiveUrl")
    private Object positiveUrl;
    @JsonProperty("backUrl")
    private Object backUrl;
}
