package com.mycompany.mall.admin.base;

/**
 * @Author: Liu Yue
 * @Date: 2025/3/30 下午4:57
 */

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DBUtil {
    private static DataSource dataSource;

    static {
        try {
            Properties props = Config.getAll(); // ✅ 从统一配置读取

            DruidDataSource ds = new DruidDataSource();
            ds.setDriverClassName(props.getProperty("driverClassName"));
            ds.setUrl(props.getProperty("url"));
            ds.setUsername(props.getProperty("username"));
            ds.setPassword(props.getProperty("password"));
            ds.setInitialSize(Integer.parseInt(props.getProperty("initialSize", "5")));
            ds.setMaxActive(Integer.parseInt(props.getProperty("maxActive", "20")));

            dataSource = ds;
        } catch (Exception e) {
            throw new RuntimeException("初始化数据库连接池失败", e);
        }
    }

    public static <T> T queryForObject(String sql, Class<T> clazz, Object... params) throws SQLException {
        return new QueryRunner(dataSource).query(sql, new BeanHandler<>(clazz), params);
    }

    public static <T> List<T> queryForList(String sql, Class<T> clazz, Object... params) throws SQLException {
        return new QueryRunner(dataSource).query(sql, new BeanListHandler<>(clazz), params);
    }

    public static int update(String sql, Object... params) throws SQLException {
        return new QueryRunner(dataSource).update(sql, params);
    }

    public static Object queryForScalar(String sql, Object... params) throws SQLException {
        return new QueryRunner(dataSource).query(sql, new ScalarHandler<>(), params);
    }
}
