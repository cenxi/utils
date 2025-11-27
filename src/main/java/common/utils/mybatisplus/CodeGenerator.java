package common.utils.mybatisplus;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.zgdz.ds.common.orm.domain.BaseEntity;

public class CodeGenerator {

    public static void main(String[] args) {

        String dbIp = "10.32.181.44";
        String dbPort = "32283";
        String dbName = "ds_system";
        String[] tables = new String[]{"service_catalog"};
        String basePackage = "com.zgdz.ds.dorado.dao";
        IdType idType = IdType.ASSIGN_ID;

        String[] tablePrefix = new String[]{"tbl_"};

        // 全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(System.getProperty("user.dir") + "/generate/src/main/java") // 设置输出目录
                .setAuthor("Your Name") // 设置作者
                .setOpen(false) // 设置生成后是否自动打开目录
                .setFileOverride(true) // 设置文件存在时是否覆盖
                .setServiceName("%sService") // 设置Service接口名后缀
                .setIdType(idType) // 设置主键生成策略
                .setSwagger2(true); // 设置是否生成Swagger注解

        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        String jdbcUrl = "jdbc:dm://{}:{}/{}?schema={}&useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&clobAsString=true";
        jdbcUrl = StrUtil.format(jdbcUrl, dbIp, dbPort, dbName, dbName);
        dataSourceConfig.setDbType(DbType.DM) // 设置数据库类型
                .setUrl(jdbcUrl) // 数据库连接URL
                .setUsername("ROOT") // 数据库用户名
                .setPassword("cGFzc0BteXNxbGFkbWlu") // 数据库密码
                .setDriverName("dm.jdbc.driver.DmDriver"); // 数据库驱动类名

        // 策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setInclude(tables) // 指定需要生成代码的表名
                .setNaming(NamingStrategy.underline_to_camel) // 设置表名转类名策略
                .setColumnNaming(NamingStrategy.underline_to_camel) // 设置列名转属性名策略
                .setEntityLombokModel(true) // 设置实体类使用Lombok模型
                .setRestControllerStyle(true) // 设置Controller使用REST风格
                .setTablePrefix(tablePrefix) // 设置表名前缀
                .setSuperEntityClass(BaseEntity.class)
                .setEntityTableFieldAnnotationEnable(true);

        // 包配置
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(basePackage) // 设置父包名
                .setMapper("mapper") // 设置Mapper接口所在的子包名
                .setEntity("entity") // 设置实体类所在的子包名
                .setController("controller") // 设置Controller所在的子包名
                .setService("service") // 设置Service所在的子包名
                .setXml("mapper"); // 设置Mapper XML文件所在的子包名

        // 模板配置
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig
                .setXml(null) // 不生成XML文件
//                .setController("templates/controller.java.vm") // 设置Controller模板路径
                .setEntity("templates/entity.java") // 设置实体类模板路径
                .setMapper("templates/mapper.java")
                .setService("templates/service.java")
                .setServiceImpl("templates/serviceImpl.java"); // 设置Mapper接口模板路径

        // 整合配置
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator.setGlobalConfig(globalConfig)
                .setDataSource(dataSourceConfig)
                .setStrategy(strategyConfig)
                .setPackageInfo(packageConfig)
                .setTemplate(templateConfig)
                .setTemplateEngine(new FreemarkerTemplateEngine());

        // 执行生成
        autoGenerator.execute();
    }
}
