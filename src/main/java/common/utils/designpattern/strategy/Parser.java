package common.utils.designpattern.strategy;

import java.io.Reader;
import java.util.List;

/**
 * @author fengxi
 * @className Parser
 * @description
 * @date 2023年01月30日 10:23
 */
public interface Parser {
    List parse(Reader r);
}
