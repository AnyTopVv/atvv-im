package com.atvv.atvvim.tcp.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户端
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserClientDto {
    private String userId;
    private Integer clientType;
}
