package com.w1nd.grainmall.member.exception;

public class MailExistException extends RuntimeException {
    public MailExistException(){
        super("手机号存在");
    }
}
