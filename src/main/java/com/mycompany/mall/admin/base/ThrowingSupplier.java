package com.mycompany.mall.admin.base;

/**
 * @Author: Liu Yue
 * @Date: 2025/4/11 上午11:38
 */
@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
