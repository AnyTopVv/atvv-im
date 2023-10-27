package com.atvv.im.common.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendShip {

    private Long id;

    private String alias;

    private Date createTime;

    private Long originId;

    private Long targetId;

    private Date updateTime;

    private Long enable;


    public FriendShip another() {
        FriendShip another = new FriendShip();
        another.setTargetId(this.getOriginId());
        another.setOriginId(this.getTargetId());
        return another;
    }
}
