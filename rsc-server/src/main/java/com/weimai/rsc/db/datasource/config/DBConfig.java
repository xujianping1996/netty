package com.weimai.rsc.db.datasource.config;

import com.weimai.rsc.Configurations;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-07-13 16:47
 */
public class DBConfig {

    private static final Configurations CONFIGURATIONS;
    private static final HikariDataSource hikariDataSource ;
    private static final HikariConfig config ;

    static {
        CONFIGURATIONS = Configurations.getInstance();
        config = new HikariConfig();

        config.setDriverClassName(CONFIGURATIONS.getDataSources().get(0).getDriverClass());
        //config.setDataSourceClassName(get("dataSource.datasourceType"));
        config.setJdbcUrl(CONFIGURATIONS.getDataSources().get(0).getJdbcUrl());
        config.setUsername(CONFIGURATIONS.getDataSources().get(0).getUsername());
        config.setPassword(CONFIGURATIONS.getDataSources().get(0).getPassword());
        hikariDataSource = new HikariDataSource(config);

    }

    private DBConfig(){}

    public static HikariDataSource hikariDataSource(){
        return hikariDataSource;
    }

}
