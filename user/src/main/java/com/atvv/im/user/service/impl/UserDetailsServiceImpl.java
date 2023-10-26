package com.atvv.im.user.service.impl;

import com.atvv.im.user.exception.UserServiceException;
import com.atvv.im.common.model.po.User;
import com.atvv.im.common.dao.UserDao;
import com.atvv.im.user.model.dto.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author hjq
 * @date 2023/9/14 20:01
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Resource
    private UserDao userDao;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名查询用户信息
        User user = userDao.findByUserName(username);
        //如果查询不到数据就通过抛出异常来给出提示
        if(Objects.isNull(user)){
            log.info("用户{}不存在",username);
            throw new UserServiceException("用户不存在");
        }

        //封装成UserDetails对象返回
        return new LoginUser(user);
    }
}
