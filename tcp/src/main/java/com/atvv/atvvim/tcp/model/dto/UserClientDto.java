package com.atvv.atvvim.tcp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName UserClientDto
 * @Description
 * @date 2023/4/25 11:18
 * @Author yanceysong
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserClientDto {
    private Integer appId;
    private String userId;
    private Integer clientType;
    private String imei;
}
