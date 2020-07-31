package com.jeesuite.rest.response;

public interface HttpCodeType {

    /**
     * 获取异常代码
     */
    public int getCode();

    /**
     * 获取异常信息
     */
    public String getMsg();
}
