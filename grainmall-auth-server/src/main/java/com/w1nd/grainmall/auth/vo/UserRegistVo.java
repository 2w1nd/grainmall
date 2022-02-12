package com.w1nd.grainmall.auth.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class UserRegistVo {
        @NotEmpty(message = "用户名必须提交")
        @Length(min = 6, max = 18, message = "用户名必须是6-18位字符")
        private String userName;

        @NotEmpty(message = "密码必须填写")
        @Length(min = 6, max = 18, message = "密码必须是6-18位字符")
        private String password;

        @NotEmpty(message = "QQ邮箱必须填写")
        // @Pattern(regexp = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\\\.[a-zA-Z0-9_-]+)+$",message = "QQ邮箱格式不正确")
        private String mail;

        @NotEmpty(message = "验证码必须填写")
        private String code;
}
