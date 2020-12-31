package common.annotations;

import java.lang.annotation.*;

/**
 * @Description 隐藏base64，或者过长字符串属性
 * @author chenchaoyun
 * @date 2017年8月8日 下午3:47:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface HideImg {

}
