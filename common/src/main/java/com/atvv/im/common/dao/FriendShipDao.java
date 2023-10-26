package com.atvv.im.common.dao;

import com.atvv.im.common.model.po.FriendShip;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FriendShipDao {

    boolean isFriend(@Param("originId") Long originId, @Param("targetId") Long targetId);

    void insert(FriendShip friendShip);
}
