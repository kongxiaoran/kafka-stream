package finchina.demo.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.gw.finchina.old.common.util.PropertyUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan(
        basePackages = "finchina.demo.mapper.gg",
        sqlSessionFactoryRef = "sqlServerSessionFactoryGG"
)
public class SQLServerDataSourceConfigGG {

    @Bean(name = "sqlServerDataSourceGG")
    public DataSource sqlServerDataSource() {
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUsername(PropertyUtil.getProperty("sqlserver.notice.username"));
        druidDataSource.setPassword(PropertyUtil.getProperty("sqlserver.notice.password"));
        druidDataSource.setUrl(PropertyUtil.getProperty("sqlserver.notice.url"));
        druidDataSource.setQueryTimeout(180);
        druidDataSource.setValidationQuery("select 'x' ");
        druidDataSource.setKeepAlive(true);
        druidDataSource.setKeepAliveBetweenTimeMillis(3700000);
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(180);
        druidDataSource.setTimeBetweenEvictionRunsMillis(3600000);

        return druidDataSource;
    }


    @Bean("sqlServerSessionFactoryGG")
    public SqlSessionFactory sqlServerSessionFactory(@Qualifier("sqlServerDataSourceGG") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/mapper/gg/*.xml"));
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        //设置sql语句输出
        configuration.setDefaultStatementTimeout(300); // 设置sql执行超时时间：：（秒）
        sqlSessionFactoryBean.setConfiguration(configuration);
        sqlSessionFactoryBean.setDataSource(dataSource);
        return sqlSessionFactoryBean.getObject();
    }
}
