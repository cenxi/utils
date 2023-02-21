package common.utils.designpattern.strategy;

/**
 * @author fengxi
 * @className ParserFactory
 * @description
 * @date 2023年01月30日 10:23
 */
public interface ParserFactory {
    Parser getParser(ContentType contentType);
}
