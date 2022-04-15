package common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fengxi
 * @date 2022年03月15日 17:23
 */
public class RegUtil {

    /**
     * 从一段文本中提取所有数据
     * @param str
     * @return
     */
    public static List<String> extractNum(String str) {
        String regEx="(\\d|\\.)+";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(str);
        List<String> nums = new ArrayList<>();
        while (matcher.find()) {
            nums.add(matcher.group());
        }
        return nums;
    }

    /**
     * 从一段文本中提出所有身份证
     * @param str
     * @return
     */
    public static List<String> extractIdCards(String str) {
        String cardReg = "(([0-9]{17}[xX0-9]{1})|([0-9]{14}[xX0-9]{1}))";
        Pattern pattern = Pattern.compile(cardReg);
        Matcher matcher=pattern.matcher(str);
        List<String> cards = new ArrayList<>();
        while (matcher.find()) {
            cards.add(matcher.group());
        }
        return cards;
    }

}
