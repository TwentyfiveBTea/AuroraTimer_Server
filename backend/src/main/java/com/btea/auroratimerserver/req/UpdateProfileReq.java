package com.btea.auroratimerserver.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/10 23:30
 * @Description: 更新用户资料请求参数
 */
@Data
public class UpdateProfileReq {

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 专业方向（可选）
     */
    private String direction;

    /**
     * 当前职位（可选）
     */
    private String position;

    /**
     * 邮箱（可选）
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 当前密码（修改密码时必填）
     */
    private String currentPassword;

    /**
     * 新密码（修改密码时必填）
     */
    @Size(min = 8, message = "密码长度至少8位")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "密码必须包含字母和数字")
    private String newPassword;
}
