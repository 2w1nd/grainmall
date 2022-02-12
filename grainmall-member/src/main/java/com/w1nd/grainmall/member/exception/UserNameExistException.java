package com.w1nd.grainmall.member.exception;

public class UserNameExistException extends RuntimeException {
    public UserNameExistException(){
        super("用户名已存在");
    }
}