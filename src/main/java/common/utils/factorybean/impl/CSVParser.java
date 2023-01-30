package common.utils.factorybean.impl;

import common.utils.factorybean.Parser;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.util.List;

/**
 * @author fengxi
 * @className CSVParser
 * @description
 * @date 2023年01月30日 10:24
 */
@Service("CSV")
public class CSVParser implements Parser {
    @Override
    public List parse(Reader r) {
        return null;
    }
}
