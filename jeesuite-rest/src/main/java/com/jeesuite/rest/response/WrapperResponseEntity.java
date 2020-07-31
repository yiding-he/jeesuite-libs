package com.jeesuite.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.jeesuite.rest.filter.RequestHeaderHolder;
import com.jeesuite.rest.utils.I18nUtils;


public class WrapperResponseEntity {

    // 状态
    private String code;

    // 返回信息
    private String msg;

    // 响应数据
    @JsonInclude(Include.NON_NULL)
    private Object data;

    @JsonIgnore
    private int httpStatus;

    @JsonIgnore
    private boolean bizException;

    public WrapperResponseEntity() {
    }

    ;

    /**
     * 构造函数
     */
    public WrapperResponseEntity(HttpCodeType httpCode) {
        this.code = String.valueOf(httpCode.getCode());
        this.msg = I18nUtils.getMessage(RequestHeaderHolder.get(), String.valueOf(code), httpCode.getMsg());
        this.httpStatus = httpCode.getCode();
    }

    /**
     * 构造函数
     */
    public WrapperResponseEntity(String errorCode, String msg, boolean bizException) {
        this.code = errorCode;
        this.msg = I18nUtils.getMessage(RequestHeaderHolder.get(), String.valueOf(code), msg);
        this.bizException = bizException;
    }

    /**
     * 获取数据
     */
    public Object getData() {
        return data;
    }

    /**
     * 获取状态
     */
    public String getCode() {
        return this.code;
    }

    /**
     * 获取信息
     */
    public String getMsg() {
        return this.msg;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int httpStatus() {
        if (httpStatus > 0) return httpStatus;
        return bizException ? 417 : 500;
    }

    @Override
    public String toString() {
        return "RestResponse [getData()=" + getData() + ", getCode()=" + getCode() + ", getMsg()=" + getMsg() + "]";
    }
}
