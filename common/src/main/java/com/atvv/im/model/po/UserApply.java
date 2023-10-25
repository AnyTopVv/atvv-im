package com.atvv.im.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("user_apply")
public class UserApply {
    private Long id;

    private Long applicantId;

    private String attach;

    private Long targetId;

    private Integer type;

    private Date createTime;

    private Long enable;
}
