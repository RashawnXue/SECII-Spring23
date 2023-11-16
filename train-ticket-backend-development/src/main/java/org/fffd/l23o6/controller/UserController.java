package org.fffd.l23o6.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.mapper.UserMapper;
import org.fffd.l23o6.pojo.vo.user.*;
import org.fffd.l23o6.service.UserService;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 用户登录
     * @param request 登录请求
     * @return success响应
     */
    @PostMapping("session")
    public CommonResponse<?> login(@Valid @RequestBody LoginRequest request) {
        // Throws BizException if auth failed.
        userService.login(request.getUsername(), request.getPassword());

        StpUtil.login(request.getUsername());
        return CommonResponse.success();
    }

    /**
     * 用户注册
     * @param request 注册请求
     * @return success响应
     */
    @PostMapping("user")
    public CommonResponse<?> register(@Valid @RequestBody RegisterRequest request) {
        // Throws BizException if register failed.
        userService.register(request.getUsername(), request.getPassword(), request.getName(), request.getIdn(), request.getPhone(), request.getType(), request.getUserType());

        return CommonResponse.success();
    }

    /**
     * 登出
     * @return success响应
     */
    @DeleteMapping("session")
    public CommonResponse<?> logout() {
        StpUtil.checkLogin();
        return CommonResponse.success(200);
    }

    /**
     * 获取用户信息
     * @return 用户信息
     */
    @GetMapping("user")
    public CommonResponse<UserVO> userInfo() {
        StpUtil.checkLogin();
        return CommonResponse.success(UserMapper.INSTANCE.toUserVO(userService.findByUserName(String.valueOf(StpUtil.getLoginId()))));
    }

    /**
     * 编辑用户信息
     * @param request 修改用户信息请求
     * @return success响应
     */
    @PutMapping("user")
    public CommonResponse<?> editInfo(@Valid @RequestBody EditUserInfoRequest request) {
        StpUtil.checkLogin();
        userService.editInfo(StpUtil.getLoginIdAsString(), request.getName(), request.getIdn(), request.getPhone(), request.getType());
        return CommonResponse.success();
    }
}