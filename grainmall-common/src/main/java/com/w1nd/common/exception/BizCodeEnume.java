package com.w1nd.common.exception;

/**
 * @Description:
 * @author: w1nd
 * @date: 2021年08月16日 13:29
 */
public enum  BizCodeEnume {
    UNKNOW_EXCEPTION(10000,"系统未知异常"),
    VAILD_EXCEPTION(10001,"参数格式校验失败"),
    PRODUCT_UP_EXCEPTION(11000, "商品上架异常");

    private int code;
    private String msg;

    BizCodeEnume(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
