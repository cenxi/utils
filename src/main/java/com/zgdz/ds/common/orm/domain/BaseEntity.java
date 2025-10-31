package com.zgdz.ds.common.orm.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.UPDATE)
    private String updateBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 逻辑删除
     */
    @TableField
    @ApiModelProperty(name = "逻辑删除: 1-未删除 0-已删除")
    private Integer deleted = 1;


    /*
     * 以下为自动设置字段标记
     */
    @TableField(exist = false)
    @JsonIgnore
    private boolean autoUpdateTime = true;

    @TableField(exist = false)
    @JsonIgnore
    private boolean autoUpdateBy = true;

    @TableField(exist = false)
    @JsonIgnore
    private boolean autoUpdateId = true;

    @TableField(exist = false)
    @JsonIgnore
    private boolean autoCreateTime = true;

    @TableField(exist = false)
    @JsonIgnore
    private boolean autoCreateBy = true;

    @TableField(exist = false)
    @JsonIgnore
    private boolean autoCreateId = true;
}
