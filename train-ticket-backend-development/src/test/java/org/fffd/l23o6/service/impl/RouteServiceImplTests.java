package org.fffd.l23o6.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.dao.*;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.StationEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.service.RouteService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RouteServiceImplTests {
    @Autowired
    UserDao userDao;
    @Autowired
    StationDao stationDao;
    @Autowired
    RouteDao routeDao;
    @Autowired
    TrainDao trainDao;
    @Autowired
    OrderDao orderDao;
    @Autowired
    RouteService routeService;

    Long route1Id = null;
    Long route2Id = null;

    @BeforeEach
    void beforeEach(){
        stationDao.deleteAll();
        routeDao.deleteAll();
        trainDao.deleteAll();
        orderDao.deleteAll();
        userDao.deleteAll();

        UserEntity user = UserEntity.builder()
                .name("王贝拉")
                .type("客户")
                .userType(UserType.ADMIN_USER)
                .username("bellakira")
                .phone("11451411451")
                .password("Bella0717")
                .points(0)
                .idn("114514114514114514")
                .build();
        userDao.save(user);
        StpUtil.login("bellakira");

        StationEntity station1 = StationEntity.builder().name("黑塔空间站").build();
        StationEntity station2 = StationEntity.builder().name("雅利洛6").build();
        StationEntity station3 = StationEntity.builder().name("仙舟罗浮").build();
        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);

        RouteEntity route1 = RouteEntity.builder()
                .name("线路1")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station2.getId());
                }})
                .build();
        routeDao.save(route1);
        route1Id = route1.getId();

        RouteEntity route2 = RouteEntity.builder()
                .name("线路2")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station2.getId());
                }})
                .build();
        routeDao.save(route2);
        route2Id = route2.getId();

        TrainEntity train1 = TrainEntity.builder()
                .date("2024.5.10")
                .trainType(TrainType.NORMAL_SPEED)
                .name("车次1")
                .arrivalTimes(new ArrayList<>(2))
                .departureTimes(new ArrayList<>(2))
                .routeId(route1.getId())
                .seats(new boolean[2][30])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train1);
    }

    @AfterEach
    void afterEach(){
        StpUtil.logout();
        userDao.deleteAll();
        stationDao.deleteAll();
        routeDao.deleteAll();
        trainDao.deleteAll();
    }

    /**
     * 测试删除路线正常流程
     */
    @Test
    void deleteRouteTest1(){
        routeService.deleteRoute(route2Id);
        assertFalse(routeDao.findById(route2Id).isPresent());
    }

    /**
     * 测试删除路线是否抛出车次数据依赖异常
     */
    @Test
    void deleteRouteTest2(){
        BizException exception = assertThrows(BizException.class, () -> routeService.deleteRoute(route1Id));
        assertEquals(BizError.TRAIN_DATA_DEPENDENCY.getCode(), exception.getCode());
    }

}
