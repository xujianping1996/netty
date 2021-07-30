package com.weimai.rsc.db.datasource.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-13 16:47
 */
public class DBConfig {

    private static final Properties properties ;
    private static final HikariDataSource hikariDataSource ;
    private static final HikariConfig config ;

    static {
        properties = new Properties();
        FileInputStream inputStream = null;  //注意路径
        try {
            inputStream = new FileInputStream("D:\\idea_project\\rsc-parent\\rsc-server\\src\\main\\resources\\db.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        config = new HikariConfig();
        config.setDriverClassName(get("dataSource.driverClass"));
        //config.setDataSourceClassName(get("dataSource.datasourceType"));
        config.setJdbcUrl(get("dataSource.jdbcUrl"));
        config.setUsername(get("dataSource.username"));
        config.setPassword(get("dataSource.password"));
        hikariDataSource = new HikariDataSource(config);

    }

    private DBConfig(){}

    private static String get(String propertiesName){
        Object o = properties.get(propertiesName);
        if (o==null){
            throw new RuntimeException("属性"+propertiesName+"缺失");
        }
        return String.valueOf(o).trim();
    }

    public static HikariDataSource hikariDataSource(){
        return hikariDataSource;
    }

    public static void main(String[] args) {
        System.out.println(get("dataSource.jdbcUrl"));
    }
}
