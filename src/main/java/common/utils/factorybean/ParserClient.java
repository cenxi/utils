package common.utils.factorybean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fengxi
 * @className Client
 * @description
 * @date 2023年01月30日 10:26
 */
@Service
public class ParserClient {

    private ParserFactory parserFactory;

    @Autowired
    public ParserClient(ParserFactory parserFactory) {
        this.parserFactory = parserFactory;
    }

    public Parser getParser(ContentType contentType) {
        // 关键点，直接根据类型获取
        return parserFactory
                .getParser(contentType);
    }

}
