package common.annotations;

import java.lang.annotation.*;

/**
 * 描述: 隐藏密码等属性
 *
 * @author chenchaoyun
 * @create 2018/11/27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface HideAnn {

}
