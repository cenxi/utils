package common.utils.designpattern.strategy.impl;

import common.utils.designpattern.strategy.Parser;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.util.List;

/**
 * @author fengxi
 * @className JSONParser
 * @description
 * @date 2023年01月30日 10:25
 */
@Service("JSON")
public class JSONParser implements Parser {

    @Override
    public List parse(Reader r) {
        return null;
    }
}
