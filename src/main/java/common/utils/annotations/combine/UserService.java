package common.utils.annotations.combine;

import javax.annotation.*;
import java.lang.annotation.*;

/**
 * @author fengxi
 * @date 2022年07月15日 16:21
 */
@MyService
public class UserService {


    public static void main(String[] args) {
        Class<UserService> clazz = UserService.class;
        getAnnos(clazz);
    }

    /**
     * interface java.lang.annotation.Documented 等 存在循环，导致内存溢出，所以需要排除java的源注解
     * @param classz
     */
    private static void getAnnos(Class<?> classz){
        Annotation[] annotations = classz.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType() != Deprecated.class &&
                    annotation.annotationType() != SuppressWarnings.class &&
                    annotation.annotationType() != Override.class &&
                    annotation.annotationType() != PostConstruct.class &&
                    annotation.annotationType() != PreDestroy.class &&
                    annotation.annotationType() != Resource.class &&
                    annotation.annotationType() != Resources.class &&
                    annotation.annotationType() != Generated.class &&
                    annotation.annotationType() != Target.class &&
                    annotation.annotationType() != Retention.class &&
                    annotation.annotationType() != Documented.class &&
                    annotation.annotationType() != Inherited.class
            ) {
                if (annotation.annotationType() == MyComponent.class){
                    System.out.println(" 存在注解 @MyComponent ");
                }else{
                    getAnnos(annotation.annotationType());
                }
            }
        }
    }
}
