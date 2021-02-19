package com.hdyl.daily.config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;


/**
 * @Author:zrt
 * @Date:2021/2/18 23:27
 * @version:1.0
 */
@Configuration
public class DataSourceConfig {

    @ConditionalOnMissingBean(name = "mysqlSource")
    @ConfigurationProperties(prefix ="spring.datasource.primary")
    public DataSource businessDbDataSource() {
        return DataSourceBuilder.create().build();
    }
}
