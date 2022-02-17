package com.w1nd.grainmall.member.interceptor;

import com.w1nd.common.constant.AuthServerConstant;
import com.w1nd.common.vo.MemberResponseVO;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberResponseVO> loginUser = new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();
        boolean status = matcher.match("/member/**", requestURI);
        if (status){
            return true;
        }


        MemberResponseVO attribute = (MemberResponseVO) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if (attribute != null){
            loginUser.set(attribute);
            return true;
        }else {
            //没登录就去登录
            request.getSession().setAttribute("msg","请先进行登录");
            response.sendRedirect("http://auth.grainmall.com/login.html");
            return false;
        }

    }
}
