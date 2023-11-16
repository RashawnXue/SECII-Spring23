package org.fffd.l23o6.service;

import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.UserType;

public interface UserService {
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     */
    void login(String username, String password);

    /**
     * 用户注册
     * @param username 用户名
     * @param password 密码
     * @param name 昵称
     * @param idn 证件号
     * @param phone 手机号
     * @param type 证件类型
     * @param userType 用户身份类型
     */
    void register(String username, String password, String name, String idn, String phone, String type, UserType userType);

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户实体
     */
    UserEntity findByUserName(String username);

    /**
     * 修改用户信息
     * @param username 被修改的用户的用户名
     * @param name 昵称
     * @param idn 证件号
     * @param phone 手机号
     * @param type 证件类型
     */
    void editInfo(String username, String name, String idn, String phone, String type);

    /**
     * 修改用户积分
     * @param id 用户ID
     * @param points 积分增量（可为负）
     * @return 用户实体
     */
    UserEntity addPoints(Long id, Integer points);

    /**
     * 检查当前用户权限
     */
    void checkAuthority();
}