package com.w1nd.grainmall.member.service.impl;

import com.w1nd.grainmall.member.dao.MemberLevelDao;
import com.w1nd.grainmall.member.entity.MemberLevelEntity;
import com.w1nd.grainmall.member.exception.MailExistException;
import com.w1nd.grainmall.member.exception.UserNameExistException;
import com.w1nd.grainmall.member.vo.MemberLoginVo;
import com.w1nd.grainmall.member.vo.MemberRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.common.utils.Query;

import com.w1nd.grainmall.member.dao.MemberDao;
import com.w1nd.grainmall.member.entity.MemberEntity;
import com.w1nd.grainmall.member.service.MemberService;


@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        // System.out.println("1" + vo);
        MemberDao memberDao = this.baseMapper;
        MemberEntity entity = new MemberEntity();
        //设置默认等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        entity.setLevelId(levelEntity.getId());

        //检查用户名和手机号是否唯一。为了让controller能感知异常，异常机制
        checkMailUnique(vo.getMail());
        checkUserNameUnique(vo.getUserName());

        entity.setEmail(vo.getMail());
        entity.setUsername(vo.getUserName());
        entity.setNickname(vo.getUserName());
        //密码加密
        // System.out.println("2" + vo);
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        entity.setPassword(encode);
        System.out.println(entity);
        memberDao.insert(entity);
    }

    @Override
    public void checkMailUnique(String mail) throws MailExistException {
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("email", mail));
        if (count > 0){
            throw new MailExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String userName) throws UserNameExistException {
        MemberDao memberDao = this.baseMapper;
        Integer count = memberDao.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));
        if (count > 0){
            throw new UserNameExistException();
        }
    }

    @Override
    public MemberEntity login(MemberLoginVo vo) {
        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        //去数据库查询
        MemberDao memberDao = this.baseMapper;
        MemberEntity memberEntity = memberDao.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("email", loginacct));
        if (memberEntity == null){
            //登录失败
            return null;
        }else {
            //1、获取到数据库的password
            String passwordDB = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //2、密码匹配
            boolean matches = passwordEncoder.matches(password, passwordDB);
            if (matches){
                return memberEntity;
            }else {
                return null;
            }
        }

    }
}