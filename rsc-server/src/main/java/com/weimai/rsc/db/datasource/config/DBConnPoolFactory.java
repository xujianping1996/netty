package com.weimai.rsc.db.datasource.config;

import com.weimai.rsc.Configurations;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 * <p>
 * 数据库连接池工厂类
 *
 * @author DiZhi
 * @since 2021-07-13 16:47
 */
public class DBConnPoolFactory {

    private static final Configurations CONFIGURATIONS;
    private static final HikariDataSource[] HIKARI_DATA_SOURCES;
    private static final HikariConfig config;

    private static final int MAX_SOURCE_SIZE = 5;

    static {
        CONFIGURATIONS = Configurations.getInstance();
        config = new HikariConfig();
        int size = CONFIGURATIONS.getDataSources().size();
        if (size > MAX_SOURCE_SIZE) {
            System.out.println("当前版本最多支持5个数据源，配置文件中前五个数据源配置将生效，其余将被忽略！");
            size = MAX_SOURCE_SIZE;
        }
        HIKARI_DATA_SOURCES = new HikariDataSource[size];
        for (int i = 0; i < size; i++) {
            config.setDriverClassName(CONFIGURATIONS.getDataSources().get(i).getDriverClass());
            //config.setDataSourceClassName(get("dataSource.datasourceType"));
            config.setJdbcUrl(CONFIGURATIONS.getDataSources().get(i).getJdbcUrl());
            config.setUsername(CONFIGURATIONS.getDataSources().get(i).getUsername());
            config.setPassword(CONFIGURATIONS.getDataSources().get(i).getPassword());
            HIKARI_DATA_SOURCES[i] = new HikariDataSource(config);
        }

    }

    private DBConnPoolFactory() {
    }

    public static HikariDataSource[] dataSources() {
        return HIKARI_DATA_SOURCES;
    }

    public static HikariDataSource dataSource(int index) {
        return HIKARI_DATA_SOURCES[index];
    }

}
