package com.aqi.configer;

import com.alibaba.druid.pool.DruidDataSource;
import com.aqi.sjdbc.MyPreciseShardingAlgorithm;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
@MapperScan(basePackages = "com.aqi.mapper.aqi", sqlSessionFactoryRef = "shardingSqlSessionFactory")
public class ShardingDataSourceConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String user;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClass;

    @Autowired
    private PaginationInterceptor paginationInterceptor;

    @Bean(name = "aqiDataSource")
    public DataSource dataSource() throws SQLException {
        return mybatisDataSource();
    }

    //分表算法
    @Resource
    private MyPreciseShardingAlgorithm myPreciseShardingAlgorithm;


    private DataSource mybatisDataSource() throws SQLException {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        /* 配置初始化大小、最小、最大 */
        dataSource.setInitialSize(1);
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(20);

        /* 配置获取连接等待超时的时间 */
        dataSource.setMaxWait(60000);

        /* 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 */
        dataSource.setTimeBetweenEvictionRunsMillis(60000);

        /* 配置一个连接在池中最小生存的时间，单位是毫秒 */
        dataSource.setMinEvictableIdleTimeMillis(300000);

//        dataSource.setValidationQuery("SELECT 'x'");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        /* 打开PSCache，并且指定每个连接上PSCache的大小。
           如果用Oracle，则把poolPreparedStatements配置为true，
           mysql可以配置为false。分库分表较多的数据库，建议配置为false */
        dataSource.setPoolPreparedStatements(false);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        /* 配置监控统计拦截的filters */
        dataSource.setFilters("stat,wall");

        return dataSource;
    }

    //创建数据源，需要把分库的库都传递进去
    //@Bean("dataSource")

    @Bean("dataSource")
    public DataSource dataSource(@Qualifier("aqiDataSource") DataSource community) throws SQLException {
        // 配置真实数据源
        Map<String, DataSource> dataSourceMap = new HashMap<String, DataSource>();
        dataSourceMap.put("aqi_china", community);
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        //如果有多个数据表需要分表，依次添加到这里
        shardingRuleConfig.getTableRuleConfigs().add(getOrderTableRuleConfiguration());
        // shardingRuleConfig.getTableRuleConfigs().add(getOrderItemTableRuleConfiguration());
        Properties p = new Properties();
        p.setProperty("sql.show", Boolean.FALSE.toString());
        // 获取数据源对象
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, p);
        return dataSource;
    }

    // 创建SessionFactory
    @Bean(name = "shardingSqlSessionFactory")
    public SqlSessionFactory shardingSqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        MybatisSqlSessionFactoryBean sessionFactory = new MybatisSqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/aqi/*.xml"));
        sessionFactory.setPlugins(new Interceptor[]{paginationInterceptor});
        return sessionFactory.getObject();
    }

    // 创建事务管理器
    @Bean("shardingTransactionManger")
    public DataSourceTransactionManager shardingTransactionManger(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    // 创建SqlSessionTemplate
    @Bean(name = "shardingSqlSessionTemplate")
    public SqlSessionTemplate shardingSqlSessionTemplate(@Qualifier("shardingSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    //订单表的分表规则配置
    private TableRuleConfiguration getOrderTableRuleConfiguration() {
        TableRuleConfiguration result = new TableRuleConfiguration("tb_aqi","aqi_china.tb_aqi_$->{0..9}");
        result.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration("uid",myPreciseShardingAlgorithm));
        return result;
    }
}
