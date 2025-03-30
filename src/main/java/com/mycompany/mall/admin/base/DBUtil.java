package com.mycompany.mall.admin.base;

/**
 * @Author: Liu Yue
 * @Date: 2025/3/30 下午4:57
 */

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class DBUtil {
    private static DataSource dataSource;

    static {
        try {
            // 1. 读取环境变量（优先用 -Denv=test 传参，否则默认 test）
            String env = System.getProperty("env", "test"); // 可设置为 dev/test/uat/prod

            String fileName = String.format("db-%s.properties", env);
            InputStream is = DBUtil.class.getClassLoader().getResourceAsStream(fileName);
            if (is == null) {
                throw new RuntimeException("未找到数据库配置文件: " + fileName);
            }

            Properties props = new Properties();
            props.load(is);

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
