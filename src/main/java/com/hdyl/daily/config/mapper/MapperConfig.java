package com.hdyl.daily.config.mapper;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @Author:zrt
 * @Date:2021/2/19 0:57
 * @version:1.0
 */
@Configuration
@MapperScan(
        basePackages = "com.hdyl.daily",
        sqlSessionTemplateRef = "majorSqlSessionTemplate"
)
public class MapperConfig {

        @Autowired
        @Qualifier("majorDataSource")
        private DataSource majorDataSource;
        /**
         * 数据工厂初始化
         *
         * @return 数据工厂
         * @throws Exception exception
         * @author gralves
         * @date 2020年5月27日 18:25:00
         */
        @Primary
        @Bean("majorSqlSessionFactory")
        public SqlSessionFactory majorSqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
            sqlSessionFactory.setDataSource(majorDataSource);
            sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().
                    getResources("classpath*:com/hdyl/daily/mapper/*.xml"));
            return sqlSessionFactory.getObject();
        }

        /**
         * 事务管理器
         *
         * @return 事务管理器
         * @author gralves
         * @date 2020年5月27日 18:57:58
         */
        @Primary
        @Bean(name = "majorTransactionManager")
        public DataSourceTransactionManager majorTransactionManager() {
            return new DataSourceTransactionManager(majorDataSource);
        }

        /**
         * sql模板
         *
         * @param sqlSessionFactory 数据工厂
         * @return sql模板
         * @author gralves
         * @date 2020年5月27日 18:58:47
         */
        @Primary
        @Bean(name = "majorSqlSessionTemplate")
        public SqlSessionTemplate majorSqlSessionTemplate(@Qualifier("majorSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
            return new SqlSessionTemplate(sqlSessionFactory);
        }
}
