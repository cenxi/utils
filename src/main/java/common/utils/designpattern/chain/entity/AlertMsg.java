package common.utils.designpattern.chain.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 报警消息
 */
@Data
public class AlertMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 图片id
     */
    private String picId;

    /**
     * 图片标签
     */
    private String label;

    /**
     * 原始图片
     */
    private String srcImage;

    /**
     * 原始图片base64
     */
    private String srcImageBase64;

    /**
     * 检测图片
     */
    private String analyzedImage;

    /**
     * 检测图片base64
     */
    private String analyzedImageBase64;


    /**
     * modelCode
     */
    private String modelCode;

    /**
     * deviceCode
     */
    private String deviceCode;

    /**
     * 可信度分数
     */
    private Float score;

    /**
     * 报警时间
     */
    private LocalDateTime alertTime;

    /**
     * 额外扩展信息(JSON对象字符串:{})
     */
    private String extraData;

    /* *************************以下为业务属性************************* */
    /**
     * 算法id(模型id)
     */
    private String modelId;

    /**
     * 算法名称
     */
    private String modelName;

    /**
     * 算法版本id(模型id)
     */
    private String modelVersionId;

    /**
     * 算法版本名称
     */
    private String modelVersionName;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备位置
     */
    private String deviceLocation;

    /**
     * 设备分组id
     */
    private String deviceGroupId;

    /**
     * 设备分组名称
     */
    private String deviceGroupName;

    /**
     * 输入源类型
     */
    private String inputType;

}
