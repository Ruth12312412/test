package com.ai.admin.common;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果
 */
@Data
@NoArgsConstructor
public class Result<T> {
    
    private Integer code;
    private String message;
    private T data;
    private Long timestamp;
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }
    
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    
    public static <T> Result<T> unauthorized() {
        return new Result<>(401, "未授权", null);
    }
    
    public static <T> Result<T> forbidden() {
        return new Result<>(403, "禁止访问", null);
    }
}