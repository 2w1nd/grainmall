package com.w1nd.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

/**
 * @Description:
 * @author: w1nd
 * @date: 2021年08月19日 10:36
 */
public class ListValueConstranintValidator implements ConstraintValidator<ListValue, Integer> {

    private Set<Integer> set = new HashSet<>();


    // 判断是否校验成功
    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] vals = constraintAnnotation.vals();
        for (int val : vals) {
            set.add(val);
        }
    }

    // 初始化方法

    /**
     *
     * @param value  需要校验的值
     * @param context   校验的上下文环境信息
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }

}
