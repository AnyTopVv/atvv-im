package com.atvv.im.common.model.po;


import com.atvv.im.common.constant.enums.CodeAdapter;
import lombok.Data;

import java.util.Date;

@Data
public class UserApply {
    private Long id;

    private Long applicantId;

    private String attach;

    private Long targetId;

    private Integer type;

    private Date createTime;

    private Integer handle;

    private Long enable;

    public enum Handle implements CodeAdapter {

        Accept(1, "接受"),
        Rejected(2, "拒绝"),
        ;

        private final Integer code;

        private final String msg;

        Handle(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        @Override
        public Integer getCode() {
            return code;
        }
    }
}
