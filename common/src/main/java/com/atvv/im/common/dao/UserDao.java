package com.atvv.im.common.dao;


import com.atvv.im.common.model.po.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author hjq
 * @date 2023/9/12 15:25
 */
@Mapper
public interface UserDao {
    int insert(User user);

    User findByUserName(@Param("username") String username);
}
