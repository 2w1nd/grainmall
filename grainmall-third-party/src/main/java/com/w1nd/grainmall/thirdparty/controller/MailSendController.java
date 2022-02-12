package com.w1nd.grainmall.thirdparty.controller;

import com.w1nd.common.utils.R;
import com.w1nd.grainmall.thirdparty.component.MailComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mail")
public class MailSendController {

    @Autowired
    MailComponent mailComponent;

    @RequestMapping("/sendcode")
    public R sendCode(@RequestParam("mail") String mail,@RequestParam("code") String code) {
        mailComponent.sendMailCode(mail, code);
        return R.ok();
    }

}
