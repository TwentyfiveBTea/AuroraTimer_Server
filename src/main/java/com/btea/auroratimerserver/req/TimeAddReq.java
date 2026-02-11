package com.btea.auroratimerserver.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/11 14:02
 * @Description: 同步工时请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeAddReq {

    /**
     * 用户ID（学号）
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 本次增加的秒数（前端固定传60）
     */
    @Min(value = 1, message = "秒数必须大于0")
    private Integer seconds;
}
