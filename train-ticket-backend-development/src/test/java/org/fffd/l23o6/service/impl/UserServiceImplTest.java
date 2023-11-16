package org.fffd.l23o6.service.impl;

import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.service.UserService;
import org.fffd.l23o6.utils.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertTrue;

@SpringBootTest
public class UserServiceImplTest {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;

    @AfterEach
    void afterEach() {
        userDao.deleteAll();
    }
    @Test
    void addPointsTest() {
        int randomPoints = TestUtil.getInstance().generateRandomNumber(0, 1000);
        UserEntity user = UserEntity.builder()
                .name("李田所")
                .type("客户")
                .userType(UserType.ADMIN_USER)
                .username("litiansuo")
                .phone("11451411451")
                .password("123456")
                .points(0)
                .idn("114514114514114514")
                .build();
        userDao.save(user);
        int originPoints = user.getPoints();
        user = userService.addPoints(user.getId(), randomPoints);
        assertEquals("修改后的积分", originPoints + randomPoints, user.getPoints());
        // 测试积分是否会出现小于0的情况
        int negPoints = TestUtil.getInstance().generateRandomNumber(-3000, -1000);
        int points = TestUtil.getInstance().generateRandomNumber(100, 1000);
        userDao.save(user.setPoints(points));
        user = userService.addPoints(user.getId(), negPoints);
        assertTrue("积分非负", user.getPoints() >= 0);
    }
}

