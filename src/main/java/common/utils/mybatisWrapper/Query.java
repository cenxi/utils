package common.utils.mybatisWrapper;

import com.baomidou.mybatisplus.core.enums.SqlKeyword;

import java.lang.annotation.*;

/**
 * @author fengxi
 * @Description TODO
 * @CreateTime 2021/8/26
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {

    SqlKeyword value() default SqlKeyword.EQ;

}
