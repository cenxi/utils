package common.utils.sql;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @author fengxi
 * @date 2022年08月02日 15:05
 */
public class SqlStrUtil {

    private static final String SPECIAL_CHAR_REGEX = "[^\u4E00-\u9FA5a-zA-Z0-9]";

    private static final Pattern NUM_START_PATTERN = Pattern.compile("^[0-9_].*$");

    /**
     * 特殊字符替换匹配: 非中文 非英文 非数字
     * 中文unicode区间：
     * 字符集	字数	Unicode 编码
     * 基本汉字	20902字	4E00-9FA5
     * 基本汉字补充	90字	9FA6-9FFF
     * 扩展A	6592字	3400-4DBF
     * 扩展B	42720字	20000-2A6DF
     * 扩展C	4153字	2A700-2B738
     * 扩展D	222字	2B740-2B81D
     * 扩展E	5762字	2B820-2CEA1
     * 扩展F	7473字	2CEB0-2EBE0
     * 扩展G	4939字	30000-3134A
     * 康熙部首	214字	2F00-2FD5
     * 部首扩展	115字	2E80-2EF3
     * 兼容汉字	477字	F900-FAD9
     * 兼容扩展	542字	2F800-2FA1D
     * PUA(GBK)部件	81字	E815-E86F
     * 部件扩展	452字	E400-E5E8
     * PUA增补	207字	E600-E6CF
     * 汉字笔画	36字	31C0-31E3
     * 汉字结构	12字	2FF0-2FFB
     * 汉语注音	43字	3105-312F
     * 注音扩展	22字	31A0-31BA
     * 〇	1字	3007
     * @param str 包含特殊字符的字符串
     * @return 以下划线替代特殊字符
     */
    public static String trimSpecialChar(String str){
        if (StringUtils.isBlank(str)){
            return str;
        }
        str = str
                .replace(" ", "")
                .replace("'", "")
                .replace("\r|\n|\t", "")
                .replaceAll(SPECIAL_CHAR_REGEX, "_");
        if (NUM_START_PATTERN.matcher(str).matches()){
            str = "v" + str;
        }
        return str;
    }
}
