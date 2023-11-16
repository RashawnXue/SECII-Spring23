package org.fffd.l23o6.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.apache.el.stream.Optional;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.StationDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.StationEntity;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.service.StationService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StationServiceImplTests {
    @Autowired
    UserDao userDao;
    @Autowired
    StationDao stationDao;
    @Autowired
    RouteDao routeDao;
    @Autowired
    StationService stationService;

    Long station1Id = null;
    Long station2Id = null;
    Long station3Id = null;

    @BeforeEach
    void beforeEach(){
        stationDao.deleteAll();
        routeDao.deleteAll();
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
        station1Id = station1.getId();
        stationDao.save(station2);
        station2Id = station2.getId();
        stationDao.save(station3);
        station3Id = station3.getId();

        RouteEntity route1 = RouteEntity.builder()
                .name("线路1")
                .stationIds(new ArrayList<>(){{
                    add(station1Id);
                    add(station2Id);
                }})
                .build();
        routeDao.save(route1);
    }

    @AfterEach
    void afterEach(){
        StpUtil.logout();
        userDao.deleteAll();
        stationDao.deleteAll();
        routeDao.deleteAll();
    }

    /**
     * 测试删除站点用例正常流程
     */
    @Test
    void deleteStationTest1(){
        stationService.deleteStation(station3Id);

        assertFalse(stationDao.findById(station3Id).isPresent());
    }

    /**
     * 测试删除站点用例是否抛出抛出路线数据依赖异常
     */
    @Test
    void deleteStationTest2(){
        BizException exception = assertThrows(BizException.class, () -> stationService.deleteStation(station1Id));
        assertEquals(BizError.ROUTE_DATA_DEPENDENCY.getCode(), exception.getCode());
    }

}
