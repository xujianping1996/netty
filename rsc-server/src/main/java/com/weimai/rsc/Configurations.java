package com.weimai.rsc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * Copyright (c) 2017 Choice, Inc. All Rights Reserved. Choice Proprietary and Confidential.
 *
 * @author DiZhi
 * @since 2021-08-13 15:06
 */

public class Configurations {

    private static Configurations CONFIGURATIONS = new Configurations();

    private Configurations() {
        InputStream
                input = null;
        try {
            input = new FileInputStream("D:\\idea_project\\rsc-parent\\rsc-server\\src\\main\\resources\\cfg.yml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Yaml yaml = new Yaml();
        Map<String ,Object> object = (Map<String ,Object>) yaml.load(input);
        loadServerConfigurations(object);
        loadDataSources(object);

    }

    private void loadDataSources(Map<String, Object> object) {
        Object o = object.get("data-source");
        List<DataSource> dataSources = new ArrayList<>();

        if (o instanceof List){
            List<Map<String,Object>> dataSourceProps = (List<Map<String,Object>>)object.get("data-source");
            for (Map<String, Object> dataSourceProp : dataSourceProps) {
                constructDataSource(dataSources, dataSourceProp);
            }
        }else {
            Map<String,Object> dataSourceProp = (Map<String,Object>)object.get("data-source");
            constructDataSource(dataSources, dataSourceProp);
        }
        this.dataSources = dataSources;
    }

    private void constructDataSource(List<DataSource> dataSources, Map<String, Object> dataSourceProp) {
        DataSource dataSource = new DataSource();
        dataSource.setDatasourceType(String.valueOf(dataSourceProp.get("datasource-type")));
        dataSource.setDriverClass(String.valueOf(dataSourceProp.get("driver-class")));
        dataSource.setPassword(String.valueOf(dataSourceProp.get("password")));
        dataSource.setJdbcUrl(String.valueOf(dataSourceProp.get("jdbc-url")));
        dataSource.setUsername(String.valueOf(dataSourceProp.get("username")));
        dataSources.add(dataSource);
    }

    private void loadServerConfigurations(Map<String, Object> object) {
        Map<String ,Object> serverProps = (Map<String ,Object>)object.get("server");
        Server server = new Server();
        server.setPort((Integer)serverProps.get("port"));
        this.server = server;
    }

    public static Configurations getInstance(){
        return CONFIGURATIONS;
    }
    private List<DataSource> dataSources;

    private Server server;

    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public void setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public List<DataSource> getDataSource() {
        return dataSources;
    }

    public void setDataSource(List<DataSource> dataSources) {
        this.dataSources = dataSources;
    }
    public class Server {
        private Integer port;

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }
    public class DataSource {
        private String driverClass;
        private String datasourceType;
        private String jdbcUrl;
        private String username;
        private String password;

        public String getDriverClass() {
            return driverClass;
        }

        public void setDriverClass(String driverClass) {
            this.driverClass = driverClass;
        }

        public String getDatasourceType() {
            return datasourceType;
        }

        public void setDatasourceType(String datasourceType) {
            this.datasourceType = datasourceType;
        }

        public String getJdbcUrl() {
            return jdbcUrl;
        }

        public void setJdbcUrl(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
