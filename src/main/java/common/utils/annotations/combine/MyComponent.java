package common.utils.annotations.combine;

import java.lang.annotation.*;

/**
 * 测试注解组合
 * @author fengxi
 * @date 2022年07月15日 16:17
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyComponent {

    String name() default "";
}
