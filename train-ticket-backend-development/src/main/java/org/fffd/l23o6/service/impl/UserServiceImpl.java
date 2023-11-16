package org.fffd.l23o6.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public void register(String username, String password, String name, String idn, String phone, String type, UserType userType) {
        UserEntity user = userDao.findByUsername(username);

        if (user != null) {
            throw new BizException(BizError.USERNAME_EXISTS);
        }

        userDao.save(UserEntity.builder().username(username).password(BCrypt.hashpw(password))
                .name(name).idn(idn).phone(phone).type(type).userType(userType).points(0).build());
    }

    @Override
    public UserEntity findByUserName(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public void login(String username, String password) {
        UserEntity user = userDao.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new BizException(BizError.INVALID_CREDENTIAL);
        }
    }

    @Override
    public void editInfo(String username, String name, String idn, String phone, String type){
        UserEntity user = userDao.findByUsername(username);
        if(user == null){
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "用户不存在");
        }
        userDao.save(user.setIdn(idn).setName(name).setPhone(phone).setType(type));
    }

    @Override
    public UserEntity addPoints(Long id, Integer points) {
        Optional<UserEntity> user = userDao.findById(id);
        if (user.isEmpty()) {
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS, "用户不存在");
        }
        int resPoints = user.get().getPoints() + points;
        if (resPoints < 0) {
            // 若修改后的积分小于0，则直接设为0
            resPoints = 0;
        }
        return userDao.save(user.get().setPoints(resPoints));
    }

    @Override
    public void checkAuthority() {
        if(userDao.findByUsername((String)(StpUtil.getLoginId())).getUserType() != UserType.ADMIN_USER){
            throw new BizException(BizError.ILLEAGAL_OPERATION);
        }
    }
}