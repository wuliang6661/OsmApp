package com.heloo.android.osmapp.model;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class BaseResult<T> {

//    1）	status: 表成功和失败状态。1表成功，0表失败。
//    2）	message: 错误信息，当有错误发生时，此errorMessage包含有错误信息
//    3）	code: 错误编码，当有错误发生时，此errorCode包含有错误编码
//    4）	data：返回数据

    private Integer status;

    private String message;

    private String code;

    private T data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean surcess() {
        return "1".equals(status);
    }

}

