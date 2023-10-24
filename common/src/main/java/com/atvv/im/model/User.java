package com.atvv.im.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author hjq
 * @date 2023/9/12 14:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    /**
     * 主键id
     */
    private Long id;
    /**
     * 用户名
     */
    private String name;
    /**
     * 用户邮箱
     */
    private String email;
    /**
     * 密码
     */
    private String password;
    /**
     * 绑定电话号码
     */
    private String phone;
    /**
     * 个性签名
     */
    private String description;
    /**
     * 状态(0表示离线,1表示在线)
     */
    private Integer state;
    /**
     * 最后一次登录ip
     */
    private String lastLoginIp;
    /**
     * 最后一次登录时间
     */
    private Date lastLoginTime;
    /**
     * 头像图片地址
     */
    private String portrait;
    /**
     * 是否被封禁(0表示否，1表示是)
     */
    private Integer isBan;
    /**
     * 注册时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 是否启用(0表示启用，enable=id表示被删除)
     */
    private Integer enable;

}
