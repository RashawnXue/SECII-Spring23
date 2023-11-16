package org.fffd.l23o6.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import io.github.lyc8503.spring.starter.incantation.exception.CommonErrorType;
import org.fffd.l23o6.dao.*;
import org.fffd.l23o6.pojo.entity.*;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.pojo.vo.train.TrainVO;
import org.fffd.l23o6.service.TrainService;
import org.fffd.l23o6.util.strategy.train.KSeriesSeatStrategy;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TrainServiceImplTests {
    @Autowired
    private TrainService trainService;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private RouteDao routeDao;
    @Autowired
    private TrainDao trainDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private UserDao userDao;

    @BeforeEach
    void before(){
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
    }

    @AfterEach
    void after(){
        StpUtil.logout();
        stationDao.deleteAll();
        routeDao.deleteAll();
        trainDao.deleteAll();
        orderDao.deleteAll();
        userDao.deleteAll();
    }

    /**
     * test func listTrains by date
     */
    @Test
    void listTrainsTest1(){
        StationEntity station1 = StationEntity.builder().name("杭州").build();
        StationEntity station2 = StationEntity.builder().name("枝江").build();
        StationEntity station3 = StationEntity.builder().name("深月").build();
        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);


        RouteEntity route1 = RouteEntity.builder()
                .name("枝江线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station2.getId());
                }})
                .build();
        routeDao.save(route1);
        RouteEntity route2 = RouteEntity.builder()
                .name("深月线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station3.getId());
                }})
                .build();
        routeDao.save(route2);
        RouteEntity route3 = RouteEntity.builder()
                .name("深月枝江线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station2.getId());
                    add(station3.getId());
                }})
                .build();
        routeDao.save(route3);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.AUGUST); // 月份从0开始，所以8月对应的值是Calendar.AUGUST
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        List<Date> arrivalTimes1 = new ArrayList<>();
        arrivalTimes1.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        arrivalTimes1.add(calendar.getTime());
        List<Date> departureTimes1 = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        departureTimes1.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        departureTimes1.add(calendar.getTime());

        List<Date> arrivalTimes2 = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        arrivalTimes2.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 26);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        arrivalTimes2.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        arrivalTimes2.add(calendar.getTime());
        List<Date> departureTimes2 = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        departureTimes2.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 26);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        departureTimes2.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        arrivalTimes2.add(calendar.getTime());


        TrainEntity train1 = TrainEntity.builder()
                .date("2023-08-20")
                .trainType(TrainType.NORMAL_SPEED)
                .name("枝江1号")
                .arrivalTimes(arrivalTimes1)
                .departureTimes(departureTimes1)
                .routeId(route1.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train1);
        TrainEntity train2 = TrainEntity.builder()
                .date("2023-08-20")
                .trainType(TrainType.NORMAL_SPEED)
                .name("深月1号")
                .arrivalTimes(arrivalTimes1)
                .departureTimes(departureTimes1)
                .routeId(route2.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train2);
        TrainEntity train3 = TrainEntity.builder()
                .date("2023-08-25")
                .trainType(TrainType.NORMAL_SPEED)
                .name("深月枝江1号")
                .arrivalTimes(arrivalTimes2)
                .departureTimes(departureTimes2)
                .routeId(route3.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train3);


        String date1 = "2023-08-20";
        String date2 = "2023-08-25";
        String date3 = "2023-08-26";

        List<TrainVO> trainVOList = trainService.listTrains(station1.getId(),station2.getId(),date1);
        assertEquals(trainVOList.size(),1);
        trainVOList = trainService.listTrains(station1.getId(),station2.getId(),date2);
        assertEquals(trainVOList.size(),1);
        trainVOList = trainService.listTrains(station1.getId(),station3.getId(),date1);
        assertEquals(trainVOList.size(),1);
        trainVOList = trainService.listTrains(station1.getId(),station3.getId(),date2);
        assertEquals(trainVOList.size(),1);
        trainVOList = trainService.listTrains(station2.getId(),station3.getId(),date1);
        assertEquals(trainVOList.size(),0);
        trainVOList = trainService.listTrains(station2.getId(),station3.getId(),date2);
        assertEquals(trainVOList.size(),0);
        trainVOList = trainService.listTrains(station2.getId(),station3.getId(),date3);
        assertEquals(trainVOList.size(),1);

    }

    /**
     * test func listTrains by route
     */
    @Test
    void listTrainsTest2(){
        StationEntity station1 = StationEntity.builder().name("杭州").build();
        StationEntity station2 = StationEntity.builder().name("枝江").build();
        StationEntity station3 = StationEntity.builder().name("深月").build();
        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);


        RouteEntity route1 = RouteEntity.builder()
                .name("枝江线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station2.getId());
                }})
                .build();
        routeDao.save(route1);
        RouteEntity route2 = RouteEntity.builder()
                .name("深月线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station3.getId());
                }})
                .build();
        routeDao.save(route2);
        RouteEntity route3 = RouteEntity.builder()
                .name("深月枝江线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station2.getId());
                    add(station3.getId());
                }})
                .build();
        routeDao.save(route3);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, Calendar.AUGUST); // 月份从0开始，所以8月对应的值是Calendar.AUGUST
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        calendar.set(Calendar.MINUTE, 0);
        List<Date> arrivalTimes1 = new ArrayList<>();
        arrivalTimes1.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        arrivalTimes1.add(calendar.getTime());
        List<Date> departureTimes1 = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 20);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        departureTimes1.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 21);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        departureTimes1.add(calendar.getTime());

        List<Date> arrivalTimes2 = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        arrivalTimes2.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 26);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        arrivalTimes2.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        arrivalTimes2.add(calendar.getTime());
        List<Date> departureTimes2 = new ArrayList<>();
        calendar.set(Calendar.DAY_OF_MONTH, 25);
        calendar.set(Calendar.HOUR_OF_DAY, 6);
        departureTimes2.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 26);
        calendar.set(Calendar.HOUR_OF_DAY, 20);
        departureTimes2.add(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 27);
        calendar.set(Calendar.HOUR_OF_DAY, 16);
        arrivalTimes2.add(calendar.getTime());


        TrainEntity train1 = TrainEntity.builder()
                .date("2023-08-20")
                .trainType(TrainType.NORMAL_SPEED)
                .name("枝江1号")
                .arrivalTimes(arrivalTimes1)
                .departureTimes(departureTimes1)
                .routeId(route1.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train1);
        TrainEntity train2 = TrainEntity.builder()
                .date("2023-08-20")
                .trainType(TrainType.NORMAL_SPEED)
                .name("深月1号")
                .arrivalTimes(arrivalTimes1)
                .departureTimes(departureTimes1)
                .routeId(route2.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train2);
        TrainEntity train3 = TrainEntity.builder()
                .date("2023-08-25")
                .trainType(TrainType.NORMAL_SPEED)
                .name("深月枝江1号")
                .arrivalTimes(arrivalTimes2)
                .departureTimes(departureTimes2)
                .routeId(route3.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train3);


        String date1 = "2023-08-20";
        String date2 = "2023-08-25";
        String date3 = "2023-08-26";

        List<TrainVO> trainVOList = trainService.listTrains(station1.getId(),station2.getId(),date1);
        assertEquals(trainVOList.size(),1);
        trainVOList = trainService.listTrains(station1.getId(),station2.getId(),date2);
        assertEquals(trainVOList.size(),1);
        trainVOList = trainService.listTrains(station1.getId(),station3.getId(),date1);
        assertEquals(trainVOList.size(),1);
        trainVOList = trainService.listTrains(station1.getId(),station3.getId(),date2);
        assertEquals(trainVOList.size(),1);
        trainVOList = trainService.listTrains(station2.getId(),station3.getId(),date3);
        assertEquals(trainVOList.size(),1);
        trainVOList = trainService.listTrains(station3.getId(),station2.getId(),date3);
        assertEquals(trainVOList.size(),0);
    }

    /**
     * test func listTrains by departureTime
     */
    @Test
    void listTrainsTest3(){
        StationEntity station1 = StationEntity.builder().name("杭州").build();
        StationEntity station2 = StationEntity.builder().name("枝江").build();
        stationDao.save(station1);
        stationDao.save(station2);

        RouteEntity route1 = RouteEntity.builder()
                .name("枝江线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station2.getId());
                }})
                .build();
        routeDao.save(route1);


        Date currentTime = new Date();
        Date earlierTime = new Date(currentTime.getTime() - (60 * 60 * 1000)); // 减一小时;
        Date laterTime = new Date(currentTime.getTime() + (60 * 60 * 1000)); // 加一小时
        //departureTime before currentTime
        List<Date> arrivalTimes1 = new ArrayList<>();
        arrivalTimes1.add(earlierTime);
        arrivalTimes1.add(new Date(earlierTime.getTime()+(60 * 60 * 1000)));
        List<Date> departureTimes1 = new ArrayList<>();
        departureTimes1.add(earlierTime);
        departureTimes1.add(new Date(earlierTime.getTime()+(60 * 60 * 1000)));
        //departureTime equals currentTime
        List<Date> arrivalTimes2 = new ArrayList<>();
        arrivalTimes2.add(currentTime);
        arrivalTimes2.add(new Date(currentTime.getTime()+(60 * 60 * 1000)));
        List<Date> departureTimes2 = new ArrayList<>();
        departureTimes2.add(currentTime);
        departureTimes2.add(new Date(currentTime.getTime()+(60 * 60 * 1000)));
        //departureTime after currentTime
        List<Date> arrivalTimes3 = new ArrayList<>();
        arrivalTimes3.add(laterTime);
        arrivalTimes3.add(new Date(laterTime.getTime()+(60 * 60 * 1000)));
        List<Date> departureTimes3 = new ArrayList<>();
        departureTimes3.add(laterTime);
        departureTimes3.add(new Date(laterTime.getTime()+(60 * 60 * 1000)));


        TrainEntity train1 = TrainEntity.builder()
                .date("2023-08-20")
                .trainType(TrainType.NORMAL_SPEED)
                .name("枝江1号")
                .arrivalTimes(arrivalTimes1)
                .departureTimes(departureTimes1)
                .routeId(route1.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train1);
        TrainEntity train2 = TrainEntity.builder()
                .date("2023-08-20")
                .trainType(TrainType.NORMAL_SPEED)
                .name("枝江2号")
                .arrivalTimes(arrivalTimes2)
                .departureTimes(departureTimes2)
                .routeId(route1.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train2);
        TrainEntity train3 = TrainEntity.builder()
                .date("2023-08-20")
                .trainType(TrainType.NORMAL_SPEED)
                .name("深月1号")
                .arrivalTimes(arrivalTimes3)
                .departureTimes(departureTimes3)
                .routeId(route1.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train3);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);

        int currentTime_year = calendar.get(Calendar.YEAR);
        int currentTime_month = calendar.get(Calendar.MONTH)+1;
        int currentTime_day = calendar.get(Calendar.DAY_OF_MONTH);

        String date =
                currentTime_year + "-" +
                (currentTime_month>=10 ? "" : "0") +
                currentTime_month+"-"+(currentTime_day>=10? "":"0")+
                currentTime_day;

        List<TrainVO> trainVOList = trainService.listTrains(station1.getId(),station2.getId(),date);
        assertEquals(trainVOList.size(),2);
        trainDao.delete(train1);
        trainVOList = trainService.listTrains(station1.getId(),station2.getId(),date);
        assertEquals(trainVOList.size(),2);
        trainDao.delete(train2);
        trainVOList = trainService.listTrains(station1.getId(),station2.getId(),date);
        assertEquals(trainVOList.size(),1);
        trainDao.delete(train3);
        trainVOList = trainService.listTrains(station1.getId(),station2.getId(),date);
        assertEquals(trainVOList.size(),0);
    }

    @Test
    void changeTrainTest(){
        StationEntity station1 = StationEntity.builder().name("杭州").build();
        StationEntity station2 = StationEntity.builder().name("枝江").build();
        StationEntity station3 = StationEntity.builder().name("深月").build();
        stationDao.save(station1);
        stationDao.save(station2);
        stationDao.save(station3);

        RouteEntity route1 = RouteEntity.builder()
                .name("枝江线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station2.getId());
                }})
                .build();
        routeDao.save(route1);
        RouteEntity route2 = RouteEntity.builder()
                .name("深月线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station3.getId());
                }})
                .build();
        routeDao.save(route2);


        List<Date> arrivalTimes1 = new ArrayList<>();
        arrivalTimes1.add(new Date(1625292000000L));
        arrivalTimes1.add(new Date(1825292000000L));
        List<Date> departureTimes1 = new ArrayList<>();
        departureTimes1.add(new Date(1725292000000L));
        departureTimes1.add(new Date(1925292000000L));


        TrainEntity train1 = TrainEntity.builder()
                .date("2023-08-20")
                .trainType(TrainType.NORMAL_SPEED)
                .name("枝江1号")
                //.prices(KSeriesSeatStrategy.INSTANCE.initPriceMap(2))
                .arrivalTimes(arrivalTimes1)
                .departureTimes(departureTimes1)
                .routeId(route1.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train1);
        TrainEntity train2 = TrainEntity.builder()
                .date("2023-05-11")
                .trainType(TrainType.NORMAL_SPEED)
                .name("深月1号")
                //.prices(KSeriesSeatStrategy.INSTANCE.initPriceMap(2))
                .arrivalTimes(arrivalTimes1)
                .departureTimes(departureTimes1)
                .routeId(route2.getId())
                .seats(new boolean[4][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train2);


        trainService.changeTrain(
                train1.getId(),
                train2.getName(),
                train2.getRouteId(),
                train2.getTrainType(),
                train2.getDate(),
                train2.getArrivalTimes(),
                train2.getDepartureTimes());

        Optional<TrainEntity> optionalTrain1 = trainDao.findById(train1.getId());
        if (optionalTrain1.isPresent()) {
            train1 = optionalTrain1.get();
        } else {
            // 处理TrainEntity对象为空的情况
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS);
        }
        Optional<TrainEntity> optionalTrain2 = trainDao.findById(train1.getId());
        if (optionalTrain2.isPresent()) {
            train2 = optionalTrain2.get();
        } else {
            // 处理TrainEntity对象为空的情况
            throw new BizException(CommonErrorType.ILLEGAL_ARGUMENTS);
        }

        assertEquals(train1.getName(),train2.getName());
        assertEquals(train1.getRouteId(),train2.getRouteId());
        assertEquals(train1.getTrainType(),train2.getTrainType());
        assertEquals(train1.getDate(),train2.getDate());
        assertEquals(train1.getArrivalTimes(),train2.getArrivalTimes());
        assertEquals(train1.getDepartureTimes(),train2.getDepartureTimes());

    }
}
