package com.mycompany.mall.admin.utils;

/**
 * @Author: Liu Yue
 * @Date: 2025/4/9 上午10:04
 */

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.utils.SourceRoot;

import java.io.File;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;

public class UrlTestedCollector {

    private static final String[] TARGET_METHODS = {
            "doPostJson", "doGet", "doPostForm", "doGetWithStatus"
    };

    private static final Map<String, String> variableMap = new HashMap<>();
    private static final Set<String> foundUrls = new HashSet<>();

    public static void main(String[] args) throws Exception {
        File projectRoot = new File("src/test/java");
        SourceRoot sourceRoot = new SourceRoot(projectRoot.toPath());

        sourceRoot.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(UrlTestedCollector::analyzeCompilationUnit);
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        System.out.println("📋 检测到的 URL:");
        foundUrls.stream().sorted().forEach(System.out::println);
        // ✅ 写入 tested_urls.txt 文件
        Files.write(Paths.get("target/coverage/tested_urls.txt"), foundUrls);
        System.out.println("\n📁 已写入 target/coverage/tested_urls.txt，供 Swagger 对比使用");
    }

    private static void analyzeCompilationUnit(CompilationUnit cu) {
        // 收集局部变量定义
        cu.findAll(VariableDeclarator.class).forEach(var -> {
            if (var.getTypeAsString().equals("String") && var.getInitializer().isPresent()) {
                String name = var.getNameAsString();
                Expression init = var.getInitializer().get();
                String resolved = resolveStringExpression(init);
                if (resolved != null) {
                    variableMap.put(name, resolved);
                }
            }
        });

        // 查找方法调用（HttpClientUtil）
        cu.findAll(MethodCallExpr.class).forEach(mce -> {
            String methodName = mce.getNameAsString();
            if (Arrays.asList(TARGET_METHODS).contains(methodName) && mce.getArguments().size() > 0) {
                Expression arg = mce.getArgument(0);
                String resolved = resolveStringExpression(arg);
                if (resolved != null && resolved.startsWith("/")) {
                    foundUrls.add(resolved);
                }
            }
        });
    }

    private static String resolveStringExpression(Expression expr) {
        if (expr.isStringLiteralExpr()) {
            return expr.asStringLiteralExpr().asString();
        } else if (expr.isBinaryExpr()) {
            BinaryExpr be = expr.asBinaryExpr();
            if (be.getOperator() == BinaryExpr.Operator.PLUS) {
                String left = resolveStringExpression(be.getLeft());
                String right = resolveStringExpression(be.getRight());

                // 保留拼接结果右侧路径
                if (right != null && right.startsWith("/")) {
                    return right;
                } else if (left != null && right != null) {
                    return left + right;
                }
            }
        } else if (expr.isNameExpr()) {
            String name = expr.asNameExpr().getNameAsString();
            return variableMap.get(name);
        }
        return null;
    }
}
