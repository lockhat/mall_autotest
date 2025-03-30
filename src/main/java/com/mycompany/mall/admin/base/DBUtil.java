package com.mycompany.mall.admin.base;

/**
 * @Author: Liu Yue
 * @Date: 2025/3/30 下午4:57
 */
import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DBUtil {
    private static DataSource dataSource;

    static {
        // 加载数据库配置（从 properties 文件或直接配置）
        Properties props = new Properties();
        props.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        props.setProperty("url", "jdbc:mysql://60.204.173.174:3306/mall?useSSL=false");
        props.setProperty("username", "reader");
        props.setProperty("password", "123456");
        props.setProperty("initialSize", "5");
        props.setProperty("maxActive", "20");

        dataSource = new DruidDataSource();
        ((DruidDataSource) dataSource).setDriverClassName(props.getProperty("driverClassName"));
        ((DruidDataSource) dataSource).setUrl(props.getProperty("url"));
        ((DruidDataSource) dataSource).setUsername(props.getProperty("username"));
        ((DruidDataSource) dataSource).setPassword(props.getProperty("password"));
        ((DruidDataSource) dataSource).setInitialSize(Integer.parseInt(props.getProperty("initialSize")));
        ((DruidDataSource) dataSource).setMaxActive(Integer.parseInt(props.getProperty("maxActive")));
    }

    /**
     * 执行查询（返回单个对象）
     */
    public static <T> T queryForObject(String sql, Class<T> clazz, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        return runner.query(sql, new BeanHandler<>(clazz), params);
    }

    /**
     * 执行查询（返回列表）
     */
    public static <T> List<T> queryForList(String sql, Class<T> clazz, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        return runner.query(sql, new BeanListHandler<>(clazz), params);
    }

    /**
     * 执行更新（INSERT/UPDATE/DELETE）
     */
    public static int update(String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        return runner.update(sql, params);
    }

    /**
     * 执行聚合查询（返回单个值，如 COUNT(*)）
     */
    public static Object queryForScalar(String sql, Object... params) throws SQLException {
        QueryRunner runner = new QueryRunner(dataSource);
        return runner.query(sql, new ScalarHandler<>(), params);
    }
}