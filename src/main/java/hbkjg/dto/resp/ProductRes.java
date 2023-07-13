package hbkjg.dto.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fengxi
 * @className ProductRes
 * @description
 * @date 2023年07月13日 14:41
 */

/**
 * {
 * 						"productId": "2266",
 * 						"productName": "门票（大人儿童均需预约）",
 * 						"productType": 1,
 * 						"holidayTip": "0",
 * 						"categoryTitle": "门票（上午9:30-12:00检票，儿童也需预约）",
 * 						"enterTypeName": "无需换票",
 * 						"childrenNum": 0,
 * 						"childrenLimit": 1,
 * 						"beginSaleDate": "00:00",
 * 						"endSaleDate": "23:59",
 * 						"appointmentType": "1",
 * 						"visitorInfo": 2,
 * 						"orderMinNum": 1,
 * 						"orderMaxNum": 4,
 * 						"identityDayLimit": 0,
 * 						"identityMaxNum": 0,
 * 						"identityMaxOrder": 0,
 * 						"sort": 99,
 * 						"createTime": "2023-07-03T03:25:39.000+00:00",
 * 						"benefitMaxNum": null,
 * 						"regionalRestrictions": null,
 * 						"regions": null,
 * 						"serviceStartTime": null,
 * 						"forwardBuyDay": null,
 * 						"forwardBuyTime": null,
 * 						"startPrice": 0,
 * 						"retailPrice": 0,
 * 						"receiptMessage": "本门票免费预约",
 * 						"shelfType": 1,
 * 						"refundTypeName": "随时退",
 * 						"buyTag": "",
 * 						"showStartText": 0,
 * 						"isShowSoldNumText": null,
 * 						"soldNum": 211557,
 * 						"soldNumText": "已售21.1万+",
 * 						"preorderType": 1,
 * 						"useStartDate": null,
 * 						"useEndDate": null,
 * 						"useCount": 1,
 * 						"useWeek": "",
 * 						"noUseDate": "",
 * 						"suitableCrowd": null,
 * 						"realName": "门票（上午9:30-12:00检票，儿童也需预约）",
 * 						"certificateInfo": "1",
 * 						"identityInfo": "1,2",
 * 						"salesStartTime": null,
 * 						"salesEndTime": null,
 * 						"enterTime": "试运行期间每周三、周四、周五、周六、周日9:30-16:30开放，15:30后停止入馆，上午票检票时间9:30-12:00，下午票检票时间12:01-15:30。所有入馆观众（含儿童）均需预约，未预约成功的观众请勿到馆。中途出馆后，可凭印记于当日15:30前再次入馆。",
 * 						"inventoryCalendar": null
 *                                        }
 */
@NoArgsConstructor
@Data
public class ProductRes {


    @JsonProperty("productId")
    private String productId;
    @JsonProperty("productName")
    private String productName;
    @JsonProperty("productType")
    private Integer productType;
    @JsonProperty("holidayTip")
    private String holidayTip;
    @JsonProperty("categoryTitle")
    private String categoryTitle;
    @JsonProperty("enterTypeName")
    private String enterTypeName;
    @JsonProperty("childrenNum")
    private Integer childrenNum;
    @JsonProperty("childrenLimit")
    private Integer childrenLimit;
    @JsonProperty("beginSaleDate")
    private String beginSaleDate;
    @JsonProperty("endSaleDate")
    private String endSaleDate;
    @JsonProperty("appointmentType")
    private String appointmentType;
    @JsonProperty("visitorInfo")
    private Integer visitorInfo;
    @JsonProperty("orderMinNum")
    private Integer orderMinNum;
    @JsonProperty("orderMaxNum")
    private Integer orderMaxNum;
    @JsonProperty("identityDayLimit")
    private Integer identityDayLimit;
    @JsonProperty("identityMaxNum")
    private Integer identityMaxNum;
    @JsonProperty("identityMaxOrder")
    private Integer identityMaxOrder;
    @JsonProperty("sort")
    private Integer sort;
    @JsonProperty("createTime")
    private String createTime;
    @JsonProperty("benefitMaxNum")
    private Object benefitMaxNum;
    @JsonProperty("regionalRestrictions")
    private Object regionalRestrictions;
    @JsonProperty("regions")
    private Object regions;
    @JsonProperty("serviceStartTime")
    private Object serviceStartTime;
    @JsonProperty("forwardBuyDay")
    private Object forwardBuyDay;
    @JsonProperty("forwardBuyTime")
    private Object forwardBuyTime;
    @JsonProperty("startPrice")
    private Integer startPrice;
    @JsonProperty("retailPrice")
    private Integer retailPrice;
    @JsonProperty("receiptMessage")
    private String receiptMessage;
    @JsonProperty("shelfType")
    private Integer shelfType;
    @JsonProperty("refundTypeName")
    private String refundTypeName;
    @JsonProperty("buyTag")
    private String buyTag;
    @JsonProperty("showStartText")
    private Integer showStartText;
    @JsonProperty("isShowSoldNumText")
    private Object isShowSoldNumText;
    @JsonProperty("soldNum")
    private Integer soldNum;
    @JsonProperty("soldNumText")
    private String soldNumText;
    @JsonProperty("preorderType")
    private Integer preorderType;
    @JsonProperty("useStartDate")
    private Object useStartDate;
    @JsonProperty("useEndDate")
    private Object useEndDate;
    @JsonProperty("useCount")
    private Integer useCount;
    @JsonProperty("useWeek")
    private String useWeek;
    @JsonProperty("noUseDate")
    private String noUseDate;
    @JsonProperty("suitableCrowd")
    private Object suitableCrowd;
    @JsonProperty("realName")
    private String realName;
    @JsonProperty("certificateInfo")
    private String certificateInfo;
    @JsonProperty("identityInfo")
    private String identityInfo;
    @JsonProperty("salesStartTime")
    private Object salesStartTime;
    @JsonProperty("salesEndTime")
    private Object salesEndTime;
    @JsonProperty("enterTime")
    private String enterTime;
    @JsonProperty("inventoryCalendar")
    private Object inventoryCalendar;
}
