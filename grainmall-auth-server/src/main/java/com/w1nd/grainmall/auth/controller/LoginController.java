package com.w1nd.grainmall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.client.utils.StringUtils;
import com.w1nd.common.constant.AuthServerConstant;
import com.w1nd.common.exception.BizCodeEnume;
import com.w1nd.common.utils.R;
import com.w1nd.common.vo.MemberResponseVO;
import com.w1nd.grainmall.auth.feign.MemberFeignService;
import com.w1nd.grainmall.auth.feign.ThirdPartFeignService;
import com.w1nd.grainmall.auth.vo.UserLoginVo;
import com.w1nd.grainmall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Controller
public class LoginController {
    @Autowired
    ThirdPartFeignService thirdPartFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;

    @ResponseBody
    @RequestMapping("/mail/sendcode")
    public R sendCode(@RequestParam("mail") String mail){
        //1、接口防刷
        String prefixPhone = AuthServerConstant.MAIL_CODE_CACHE_PREFIX + mail;
        String redisCode = stringRedisTemplate.opsForValue().get(prefixPhone);
        if (!StringUtils.isEmpty(redisCode)){
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() -l < 60000){
                //60秒内不能再发
                return R.error(BizCodeEnume.MAIL_CODE_EXCEPTION.getCode(),BizCodeEnume.MAIL_CODE_EXCEPTION.getMsg());
            }
        }

        //2、验证码的再次校验。redis 存key-phone, value-code   sms:code:18896736055 ->12345
        String code = String.valueOf((int)((Math.random() + 1) * 100000));
        //redis缓存验证码   防止同一个phone在60s内再次发送验证码  set(K var1, V var2, long var3, TimeUnit var5)
        stringRedisTemplate.opsForValue().set(prefixPhone,code + "_" + System.currentTimeMillis(),10, TimeUnit.MINUTES);
        thirdPartFeignService.sendCode(mail,code);
        return R.ok();
    }

    /**
     * //TODO 重定向携带数据，利用session原理。将数据放在session中。只要跳到下一个页面，取出数据以后，session里面的数据就会删掉
     * //TODO  1、分布式下的session问题
     * RedirectAttributes redirectAttributes 模拟重定向携带数据
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @RequestMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result,
                         RedirectAttributes redirectAttributes){
        System.out.println("进入regist方法");
        if (result.hasErrors()) {
            /**
             * 方法一
             * Map<String, String> errors = result.getFieldErrors().stream().map(fieldError ->{
             *                 String field = fieldError.getField();
             *                 String defaultMessage = fieldError.getDefaultMessage();
             *                 errors.put(field,defaultMessage);
             *                 return errors;
             *             }).collect(Collector.asList());
             */
            System.out.println(result.getFieldError().getDefaultMessage());
            //方法二：
            //1.1 如果校验不通过，则封装校验结果
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            //1.2 将错误信息封装到session中
            redirectAttributes.addFlashAttribute("errors",errors);
            /**
             * 使用 return "forward:/reg.html"; 会出现
             * 问题：Request method 'POST' not supported的问题
             * 原因：用户注册-> /regist[post] ------>转发/reg.html (路径映射默认都是get方式访问的)
             * 校验出错转发到注册页
             */
            //return "reg";    //转发会出现重复提交的问题，不要以转发的方式
            //使用重定向  解决重复提交的问题。但面临着数据不能携带的问题，就用RedirectAttributes
            return "redirect:http://auth.grainmall.com/reg.html";
        }

        //1、校验验证码
        String code = vo.getCode();
        String s = stringRedisTemplate.opsForValue().get(AuthServerConstant.MAIL_CODE_CACHE_PREFIX + vo.getMail());
        if (!StringUtils.isEmpty(s)) {
            if (code.equals(s.split("_")[0])) {
                //验证码通过,删除缓存中的验证码；令牌机制
                stringRedisTemplate.delete(AuthServerConstant.MAIL_CODE_CACHE_PREFIX + vo.getMail());
                //真正注册调用远程服务注册
                R r = memberFeignService.regist(vo);
                if (r.getCode() == 0) {
                    //成功
                    return "redirect:http://auth.grainmall.com/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg", new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                }
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.grainmall.com/reg.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            //校验出错转发到注册页
            return "redirect:http://auth.grainmall.com/reg.html";
        }

        //注册成功回到登录页
        //return "redirect:http://auth.grainmall.com/login.html";
        return "redirect:http://auth.grainmall.com/login.html";
    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session){
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute == null) {
            //没登录
            return "login";
        } else{
            return "redirect:http://grianmall.com";
        }
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session){
        //远程登录
        R login = memberFeignService.login(vo);
        if (login.getCode() == 0){
            MemberResponseVO data = login.getData("data", new TypeReference<MemberResponseVO>() {
            });
            session.setAttribute(AuthServerConstant.LOGIN_USER, data);
            //成功
            return "redirect:http://grainmall.com";
        }else{
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",login.getData("msg",new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.grainmall.com/login.html";
        }
    }
}
