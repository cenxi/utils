package common.utils.annotations.combine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解组合的使用
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@MyComponent
public @interface MyService {
}
