package com.atvv.im.model.pack;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @ClassName LoginPack
 * @Description
 * @date 2023/4/24 16:29
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@EqualsAndHashCode(callSuper= false)
@ToString(doNotUseGetters=true)
public class LoginPack {
    private String userId;
}
