package common.utils.charset;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author fengxi
 * @date 2022年08月02日 15:11
 */
@Slf4j
public class CharsetUtil {

    public static Reader detectToUtf8Reader(byte[] textBytes){
        CharsetDetector detector = new CharsetDetector();
        CharsetMatch match = detector.setText(textBytes).detect();
        String charset = "UTF-8".equals(match.getName()) ? "UTF-8" : "GBK";
        try {
            return new StringReader(new String(textBytes, charset));
        } catch (UnsupportedEncodingException e) {
            log.error("detectToUtf8 error", e);
            return new InputStreamReader(new ByteArrayInputStream(textBytes));
        }
    }

    public static byte[] detectToUtf8(byte[] textBytes){
        CharsetDetector detector = new CharsetDetector();
        CharsetMatch match = detector.setText(textBytes).detect();
        String charset = "UTF-8".equals(match.getName()) ? "UTF-8" : "GBK";
        try {
            return new String(textBytes, charset).getBytes(StandardCharsets.UTF_8.displayName());
        } catch (UnsupportedEncodingException e) {
            log.error("detectToUtf8 error", e);
            return textBytes;
        }
    }
}
