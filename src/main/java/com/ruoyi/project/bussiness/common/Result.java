package com.ruoyi.project.bussiness.common;


import lombok.*;
import java.io.Serializable;

/**
 * @author mike
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -2311929614861110456L;

    private int code;

    private String msg;

    private T data;

    public static <T> Result<T> ok() {
        return restResult(null, 0, null);
    }

    public static <T> Result<T> ok(T data) {
        return restResult(data, 0, null);
    }

    public static <T> Result<T> ok(T data, String msg) {
        return restResult(data, 0, msg);
    }

    public static <T> Result<T> failed() {
        return restResult(null, 1, null);
    }

    public static <T> Result<T> failed(String msg) {
        return restResult(null, 1, msg);
    }

    public static <T> Result<T> failed(T data) {
        return restResult(data, 1, null);
    }

    public static <T> Result<T> failed(T data, String msg) {
        return restResult(data, 1, msg);
    }

    private static <T> Result<T> restResult(T data, int code, String msg) {
        Result<T> apiResult = new Result<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg == null ? (code == 0 ? "ok" : "failed") : msg);
        return apiResult;
    }

}

