package com.atvv.im.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("friendship")
public class FriendShip {

    private Long id;

    private String alias;

    private Date createTime;

    private Long originId;

    private Long targetId;

    private Date updateTime;

    private Long enable;
}
