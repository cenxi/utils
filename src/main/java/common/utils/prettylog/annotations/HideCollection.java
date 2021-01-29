package common.utils.prettylog.annotations;

import java.lang.annotation.*;

/**
 * 描述: 隐藏集合collection、map 元素
 *
 * @author chenchaoyun
 * @create 2018/11/27
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface HideCollection {

}
