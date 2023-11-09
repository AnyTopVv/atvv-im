package com.atvv.im.user.interceptor;

import com.atvv.im.common.model.dto.CurrentUserInfo;
import com.atvv.im.common.utils.JwtUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UserInfoInterceptor implements HandlerInterceptor {

	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler
	) throws Exception {
		String token = request.getHeader("authorization");
		// 考虑到可能存在无token的情况
		if (token != null && !token.isEmpty()) {
			CurrentUserInfo.set(JwtUtil.getLoginUserId(token));
		}
		return true;
	}

	@Override
	public void postHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler,
			ModelAndView modelAndView
	) throws Exception {
		CurrentUserInfo.remove();
	}
}
