package common.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class IDCardUtil {

    private static Map<String, String> zoneNum;

    private static Map<String, String> checkZoneNum(){
        if (zoneNum == null){
            synchronized (IDCardUtil.class){
                if (zoneNum == null){
                    try(JSONReader jsonReader = new JSONReader(new InputStreamReader(IDCardUtil.class.getResourceAsStream("/city_code.json")))) {
                        long t1 = System.currentTimeMillis();
                        JSONArray array = jsonReader.readObject(JSONArray.class);
                        zoneNum = array.stream().collect(Collectors.toMap(s-> ((JSONObject)s).getString("code"), s-> ((JSONObject)s).getString("value")));
                        long t2 = System.currentTimeMillis();
                        log.info("read city_code.json completed {}ms",(t2-t1));
                    } catch (Exception e) {
                        zoneNum = new HashMap<>();
                        log.error("read city_code.json error", e);
                    }
                }
            }
        }
        return zoneNum;
    }

    /**
     * 通过身份证号码获取出生日期、性别、籍贯代码、籍贯地址
     * @param cardNo 15位或18位身份证号 ， 简单校验15或18位，每位均为数字
     * @return 返回的出生日期格式：1990-01-01，   性别格式：FEMALE-女，MALE-男， 籍贯代码:420110, 籍贯：湖北
     */
    public static IDCardUtil.IDCardInfo info(String cardNo) {
        String birthday = "";
        IDCardUtil.IDCardGender sexCode = IDCardUtil.IDCardGender.UNKNOW;
        String birthAddressCode = "";
        String birthAddress = "";
        if (!valid(cardNo)){
            return new IDCardUtil.IDCardInfo(birthday, sexCode, birthAddressCode, birthAddress);
        }
        if (cardNo.length() == 15) {
            birthday = "19" + cardNo.substring(6, 8) + "-"
                    + cardNo.substring(8, 10) + "-"
                    + cardNo.substring(10, 12);
            sexCode = Integer
                    .parseInt(cardNo.substring(cardNo.length() - 3, cardNo.length())) % 2
                    == 0 ? IDCardUtil.IDCardGender.FEMALE : IDCardUtil.IDCardGender.MALE;
        } else if (cardNo.length() == 18) {
            birthday = cardNo.substring(6, 10) + "-"
                    + cardNo.substring(10, 12) + "-"
                    + cardNo.substring(12, 14);
            sexCode = Integer
                    .parseInt(cardNo.substring(cardNo.length() - 4, cardNo.length() - 1))
                    % 2 == 0 ? IDCardUtil.IDCardGender.FEMALE : IDCardUtil.IDCardGender.MALE;
        }
        birthAddressCode = cardNo.substring(0, 6);
        birthAddress = checkZoneNum().getOrDefault(birthAddressCode, "");
        return new IDCardUtil.IDCardInfo(birthday, sexCode, birthAddressCode, birthAddress);
    }

    /**
     * 简单校验，仅校验数字和长度
     * @param cardNo
     * @return
     */
    public static boolean valid(String cardNo){
        if (null == cardNo){
            return false;
        }
        char[] number = cardNo.toCharArray();
        if (number.length == 15) {
            for (int x = 0; x < number.length; x++) {
                if (!Character.isDigit(number[x])){
                    return false;
                }
            }
        } else if (number.length == 18) {
            for (int x = 0; x < number.length - 1; x++) {
                if (!Character.isDigit(number[x])){
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    @Data
    @AllArgsConstructor
    public static class IDCardInfo{
        private String birthDay;
        private IDCardUtil.IDCardGender gender;
        private String birthAddressCode;
        private String birthAddress;
    }

    public static enum IDCardGender{
        MALE,
        FEMALE,
        UNKNOW;
    }
}
