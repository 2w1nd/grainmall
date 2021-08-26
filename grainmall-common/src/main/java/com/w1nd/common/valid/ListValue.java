package com.w1nd.common.valid;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
// 使用哪个校验器
@Constraint(
        validatedBy = {ListValueConstranintValidator.class}
)
// 注解可以放在那些地方
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
// 注解的运行时机
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {
    String message() default "{com.w1nd.common.valid.ListValue.message}";  // 校验出错，错误信息的来源

    Class<?>[] groups() default {};  // 支持分组校验的功能

    Class<? extends Payload>[] payload() default {};  // 支持一些负载信息

    int[] vals() default { };
}
