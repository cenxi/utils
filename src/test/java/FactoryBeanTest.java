import common.utils.designpattern.strategy.ContentType;
import common.utils.designpattern.strategy.ParserClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fengxi
 * @className FactoryBeanTest
 * @description
 * @date 2023年01月30日 10:28
 */
public class FactoryBeanTest extends BaseTest {

    @Autowired
    private ParserClient parserClient;

    @Test
    public void test1() {

        System.out.println(parserClient.getParser(ContentType.CSV));

    }
}
