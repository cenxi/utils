import common.utils.bigfile.BigFileReader;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

/**
 * @author ：fengxi
 * @date ：Created in 2021/1/5 2:13 下午
 * @description：大文本文件读取工具使用姿势
 * @modified By：
 */
public class BigFileReaderTest {

    @Test
    public void testBigFileRead(){
        File file = new File("/Users/fengxi/Desktop/tmp2/106-20200501.csv");
        BigFileReader bigFileReader = new BigFileReader(file, (line) -> {
            System.out.println(line);
        }, Charset.forName("utf-8"), 10 * 1024 * 1024, 1);


    }
}
