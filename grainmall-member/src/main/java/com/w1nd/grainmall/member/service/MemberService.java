package com.w1nd.grainmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.grainmall.member.entity.MemberEntity;
import com.w1nd.grainmall.member.exception.MailExistException;
import com.w1nd.grainmall.member.exception.UserNameExistException;
import com.w1nd.grainmall.member.vo.MemberLoginVo;
import com.w1nd.grainmall.member.vo.MemberRegistVo;

import java.util.Map;

/**
 * 会员
 *
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:43:19
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);

    void checkMailUnique(String phone) throws MailExistException;

    void checkUserNameUnique(String userName) throws UserNameExistException;

    MemberEntity login(MemberLoginVo vo);
}

