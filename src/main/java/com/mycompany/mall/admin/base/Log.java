package com.mycompany.mall.admin.base;

/**
 * @Author: Liu Yue
 * @Date: 2025/3/30 下午7:45
 */

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class Log {
    public static Logger get(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
