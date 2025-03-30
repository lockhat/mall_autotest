package com.mycompany.mall.admin.framework;

/**
 * @Author: Liu Yue
 * @Date: 2025/3/30 下午5:14
 */
import com.mycompany.mall.admin.base.DBUtil;
import com.mycompany.mall.admin.entity.UmsMember;

import java.sql.SQLException;
import java.util.List;

public class DBTest {
    public static void main(String[] args) {
//        try {
//            // 测试 SQL 语句
//            String sql = "SELECT password FROM ums_member where username = \'test\'";
//            Object result = DBUtil.queryForScalar(sql);
//            System.out.println("连接成功，查询结果是: " + result);
//        } catch (SQLException e) {
//            System.out.println("数据库连接或查询失败！");
//            e.printStackTrace();
//        }
        try {
            // 测试 SQL 语句
            String sql = "SELECT * FROM ums_member WHERE username = ?";
            List<UmsMember> members = DBUtil.queryForList(sql, UmsMember.class, "test");

            for (UmsMember m : members) {
                System.out.println(m);
            }
        } catch (SQLException e) {
            System.out.println("数据库连接或查询失败！");
            e.printStackTrace();
        }

    }
}

