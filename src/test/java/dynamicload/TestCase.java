package dynamicload;

import common.utils.dynamicload.ApplicationUtil;
import common.utils.dynamicload.DynamicClassLoader;
import dynamicload.service.TestService;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author fengxi
 * @date 2022年07月01日 14:49
 */
public class TestCase implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Test
    public String loadIntoSpring() throws Exception {
        String javaSrc = "package com;" +
                "public class TestClass extends com.cx.demo.dyncomp.service.TestService{" +
                " public String sayHello(String msg) {" +
                "   return \"我查到了数据,\"+dao.query(msg);" +
                " }" +
                "}";
        /**
         * 美滋滋的注册源码到spring容器得到一个对象
         * ApplicationUtil.register(applicationContext, javaSrc);
         */
        ApplicationUtil.register(applicationContext, "testClass", javaSrc);
        /**
         * 从spring上下文中拿到指定beanName的对象
         * 也可以 TestService testService = ApplicationUtil.getBean(applicationContext,TestService.class);
         */
        TestService testService = ApplicationUtil.getBean(applicationContext, "testClass");

        /**
         * 直接调用
         */
        return testService.sayHello("haha");
    }

    public String autoLoad() throws Exception {
        String javaSrc = "package com.cx.demo.dyncomp;\n" +
                "\n" +
                "import org.springframework.util.StringUtils;\n" +
                "\n" +
                "/**\n" +
                " * @author fengxi\n" +
                " * @date 2022年07月01日 10:54\n" +
                " */\n" +
                "public class TestA {\n" +
                "\n" +
                "    private String str;\n" +
                "\n" +
                "    public String fun(String str) {\n" +
                "        if (StringUtils.isEmpty(str)) {\n" +
                "            return \"empty\";\n" +
                "        } else {\n" +
                "            return \"reply:\" + str;\n" +
                "        }\n" +
                "    }\n" +
                "}\n";
        Class clazz = DynamicClassLoader.load(javaSrc);
        Object a = clazz.newInstance();
        Field field = ReflectionUtils.findField(clazz, "str");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, a, javaSrc);

        Method method = ReflectionUtils.findMethod(clazz, "fun", String.class);
        String res = (String) method.invoke(a, "aaa");
        System.out.println(res);

        return ReflectionUtils.getField(field, a).toString();
    }


}
