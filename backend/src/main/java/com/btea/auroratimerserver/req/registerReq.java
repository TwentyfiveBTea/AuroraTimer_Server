package com.btea.auroratimerserver.req;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @Author: TwentyFiveBTea
 * @Date: 2026/2/9 16:39
 * @Description: 注册请求类
 */
@Data
public class RegisterReq {

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 学号（11位数字）
     */
    @NotBlank(message = "学号不能为空")
    @Pattern(regexp = "^\\d{11}$", message = "学号必须是11位数字")
    private String userId;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码（至少8位，且包含字母和数字）
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码至少8位")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "密码必须包含字母和数字")
    private String password;

    /**
     * 确认密码（至少8位，且包含字母和数字）
     */
    @NotBlank(message = "确认密码不能为空")
    @Size(min = 8, message = "确认密码至少8位")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).+$", message = "确认密码必须包含字母和数字")
    private String confirmPassword;

    /**
     * 方向
     */
    private String direction;
}
