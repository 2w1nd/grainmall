package com.w1nd.grainmall.product.exception;

import com.w1nd.common.exception.BizCodeEnume;
import com.w1nd.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description:
 * @author: w1nd
 * @date: 2021年08月16日 13:13
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.w1nd.grainmall.product.controller")
public class GrainmallExceptionControllerAdvice {

    @ExceptionHandler(value= MethodArgumentNotValidException.class)   // 告诉这个方法接收哪些异常
    public R idException (MethodArgumentNotValidException e){
        log.error("数据校验出现问题{}, 异常类型:{}", e.getMessage(), e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError) -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(), BizCodeEnume.VAILD_EXCEPTION.getMsg()).put("data", errorMap);
    }

    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable) {
        log.error("错误：", throwable);

        return R.error(BizCodeEnume.UNKNOW_EXCEPTION.getCode(),BizCodeEnume.UNKNOW_EXCEPTION.getMsg());
    }
}
