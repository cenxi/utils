package common.utils.msgqueue.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MsgListener {

    String topic();

    String group() default "msgDefaultGroup";

    int concurrency() default 1;

    boolean topicNameWithNacosSpace() default false;

    boolean batch() default false;

    int batchSize() default 100;
}
