package com.btea.auroratimerserver.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 18:13
 * @Description: 忘记密码请求参数
 */
@Data
public class ResetPasswordReq {

    /**
     * 学号
     */
    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "^[0-9]{11}$", message = "学号格式不正确")
    private String userId;
}
