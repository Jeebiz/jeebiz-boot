package io.hiwepy.boot.sample;


import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.sql.SQLException;
import java.util.*;

class MapperGenerator {

    // 设置你的昵称
    private static String AUTHOR = "wandl";
    // 设置项目名称
    private static String PROJECT_NAME = "jeebiz-boot-sample-druid";
    // 设置项目包路径
    private static String PROJECT_PACKAGE = "io.hiwepy.boot.sample";

    /**
     * 数据源配置
     */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG = new DataSourceConfig.Builder(
            "jdbc:mysql://192.168.1.170:13306/t_demo?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&serverTimezone=GMT%2B8",
            "root", "L4BHtMCyxeRm8JTm");

    /**
     * 执行 run
     */
    public static void main(String[] args) throws SQLException {

        String MAPPER_XML = PROJECT_NAME + "/src/main/resources/mapper/mysql";
        String OUTPUT_DIR = PROJECT_NAME + "/src/main/java";

        Map<OutputFile, String> map = new HashMap<>(4);
        map.put(OutputFile.xml, MAPPER_XML);

        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                // 全局配置
                .globalConfig((scanner, builder) -> builder.author(AUTHOR).fileOverride()
                        .outputDir(OUTPUT_DIR))
                // 包配置
                .packageConfig((scanner, builder) -> builder
                        .parent(PROJECT_PACKAGE)
                        .entity("dao.entities")
                        .mapper("dao")
                        .controller("web.mvc")
                        .pathInfo(map))
                //.templateConfig((scanner, builder) -> builder.disable(TemplateType.CONTROLLER, TemplateType.SERVICE, TemplateType.SERVICEIMPL))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder
                        .addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔？所有输入 all"))).entityBuilder().enableLombok()
                        .formatFileName("%sEntity").enableChainModel().mapperBuilder().enableBaseColumnList()
                        .enableBaseResultMap().formatMapperFileName("%sMapper").build())

                .execute();
    }

    /**
     * 处理 all 情况
     *
     * @param tables
     * @return
     */
    private static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}
