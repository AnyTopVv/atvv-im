package com.atvv.im.user.dao;


import com.atvv.im.common.model.po.UserApply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserApplyDao {
    List<UserApply> find(UserApply userApply);

    int insert(UserApply userApply);

    UserApply findById(@Param("id") Long id);

    int updateHandle(@Param("id") Long id, @Param("handle") Integer handle);
}
