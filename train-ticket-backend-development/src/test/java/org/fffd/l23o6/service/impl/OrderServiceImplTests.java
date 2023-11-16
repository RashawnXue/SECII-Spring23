package org.fffd.l23o6.service.impl;

import com.alipay.api.AlipayApiException;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.dao.*;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.*;

import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.enum_.TrainType;
import org.fffd.l23o6.pojo.enum_.UserType;
import org.fffd.l23o6.service.OrderService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderServiceImplTests {
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserDao userDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private RouteDao routeDao;
    @Autowired
    private TrainDao trainDao;
    @Autowired
    private OrderDao orderDao;

    Long user1Id = null;
    Long station1Id = null;
    Long station2Id = null;
    Long route1Id = null;
    Long train1Id = null;

    @BeforeAll
    void beforeAll(){
        userDao.deleteAll();
        stationDao.deleteAll();
        routeDao.deleteAll();
        trainDao.deleteAll();
        orderDao.deleteAll();

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
        user1Id = user.getId();

        StationEntity station1 = StationEntity.builder().name("杭州").build();
        StationEntity station2 = StationEntity.builder().name("枝江").build();
        stationDao.save(station1);
        station1Id = station1.getId();
        stationDao.save(station2);
        station2Id = station2.getId();

        RouteEntity route1 = RouteEntity.builder()
                .name("枝江线")
                .stationIds(new ArrayList<>(){{
                    add(station1.getId());
                    add(station2.getId());
                }})
                .build();
        routeDao.save(route1);
        route1Id = route1.getId();

        TrainEntity train1 = TrainEntity.builder()
                .date("2022.5.10")
                .trainType(TrainType.NORMAL_SPEED)
                .name("枝江1号")
                .arrivalTimes(new ArrayList<>(2))
                .departureTimes(new ArrayList<>(2))
                .routeId(route1.getId())
                .seats(new boolean[2][56])
                .extraInfos(new ArrayList<>(2))
                .build();
        trainDao.save(train1);
        train1Id = train1.getId();
    }

    @BeforeEach
    void beforeEach(){
        orderDao.deleteAll();
    }

    @AfterAll
    void afterAll(){
        userDao.deleteAll();
        stationDao.deleteAll();
        routeDao.deleteAll();
        trainDao.deleteAll();
        orderDao.deleteAll();
    }

    /**
     * 测试创建订单用例
     */
    @Test
    void createOrderTest(){
        UserEntity user1 = userDao.findById(user1Id).get();
        StationEntity station1 = stationDao.findById(station1Id).get();
        StationEntity station2 = stationDao.findById(station2Id).get();
        TrainEntity train1 = trainDao.findById(train1Id).get();

        orderService.createOrder(user1.getUsername(), train1.getId(), station1.getId(), station2.getId(), "软卧", 1L);

        OrderEntity order = orderDao.findAll().get(0);
        assertEquals(user1.getId(), order.getUserId());
        assertEquals(train1.getId(), order.getTrainId());
        assertEquals(station1.getId(), order.getDepartureStationId());
        assertEquals(station2.getId(), order.getArrivalStationId());
        assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());
        //assertEquals(0, order.getPoints());
    }

    /**
     * 测试支付订单用例的正常流程，不使用积分抵扣
     */
    @Test
    void payOrderTest1() throws AlipayApiException {
        UserEntity user1 = userDao.findById(user1Id).get();
        OrderEntity order = OrderEntity.builder()
                .userId(user1Id)
                .trainId(train1Id)
                .departureStationId(station1Id)
                .arrivalStationId(station2Id)
                .status(OrderStatus.PENDING_PAYMENT)
                .seat("1车1座")
                .price(BigDecimal.valueOf(100))
                .points(0)
                .build();
        orderDao.save(order);

        int originPoints = user1.getPoints();

        orderService.payOrder(order.getId(), PaymentType.SCAN_QRCODE, false);

        int resultPoints = userDao.findById(user1Id).get().getPoints();
        order = orderDao.findById(order.getId()).get();

        // assertEquals(originPoints + 20, resultPoints);
        // assertEquals(OrderStatus.PAID, order.getStatus());
        // assertEquals(20, order.getPoints());
        assertEquals(BigDecimal.valueOf(100).setScale(2), order.getPrice());
    }

    /**
     * 测试支付订单用例的正常流程，使用积分抵扣
     */
    @Test
    void payOrderTest2() throws AlipayApiException {
        UserEntity user1 = userDao.findById(user1Id).get();
        OrderEntity order = OrderEntity.builder()
                .userId(user1Id)
                .trainId(train1Id)
                .departureStationId(station1Id)
                .arrivalStationId(station2Id)
                .status(OrderStatus.PENDING_PAYMENT)
                .seat("1车1座")
                .price(BigDecimal.valueOf(100))
                .points(0)
                .build();
        orderDao.save(order);

        int originPoints = user1.getPoints();

        orderService.payOrder(order.getId(), PaymentType.SCAN_QRCODE, true);

        int resultPoints = userDao.findById(user1Id).get().getPoints();
        order = orderDao.findById(order.getId()).get();

        // assertEquals(originPoints + 20, resultPoints);
        // assertEquals(OrderStatus.PAID, order.getStatus());
        // assertEquals(20, order.getPoints());
        // assertEquals(BigDecimal.valueOf(100).setScale(2), order.getPrice());
    }

    /**
     * 测试支付订单用例是否抛出非法订单状态异常
     */
    @Test
    void payOrderTest3(){
        final OrderEntity order = OrderEntity.builder()
                .userId(user1Id)
                .trainId(train1Id)
                .departureStationId(station1Id)
                .arrivalStationId(station2Id)
                .status(OrderStatus.PAID)
                .seat("1车1座")
                .price(BigDecimal.valueOf(100))
                .points(20)
                .build();
        orderDao.save(order);

        BizException exception = assertThrows(BizException.class, () -> orderService.payOrder(order.getId(), PaymentType.SCAN_QRCODE, true));
        assertEquals(BizError.ILLEAGAL_ORDER_STATUS.getCode(), exception.getCode());
    }

    /**
     * 测试支付订单用例是否抛出非法订单状态异常
     */
    @Test
    void payOrderTest4(){
        final OrderEntity order = OrderEntity.builder()
                .userId(user1Id)
                .trainId(train1Id)
                .departureStationId(station1Id)
                .arrivalStationId(station2Id)
                .status(OrderStatus.COMPLETED)
                .seat("1车1座")
                .price(BigDecimal.valueOf(100))
                .points(20)
                .build();
        orderDao.save(order);

        BizException exception = assertThrows(BizException.class, () -> orderService.payOrder(order.getId(), PaymentType.SCAN_QRCODE, true));
        assertEquals(BizError.ILLEAGAL_ORDER_STATUS.getCode(), exception.getCode());
    }

    /**
     * 测试支付订单用例是否抛出非法订单状态异常
     */
    @Test
    void payOrderTest5(){
        final OrderEntity order = OrderEntity.builder()
                .userId(user1Id)
                .trainId(train1Id)
                .departureStationId(station1Id)
                .arrivalStationId(station2Id)
                .status(OrderStatus.CANCELLED)
                .seat("1车1座")
                .price(BigDecimal.valueOf(100))
                .points(20)
                .build();
        orderDao.save(order);

        BizException exception = assertThrows(BizException.class, () -> orderService.payOrder(order.getId(), PaymentType.SCAN_QRCODE, true));
        assertEquals(BizError.ILLEAGAL_ORDER_STATUS.getCode(), exception.getCode());
    }

    /**
     * 测试取消订单用例的正常流程，取消已支付的订单
     */
    @Test
    void cancelOrderTest1(){
        UserEntity user1 = userDao.findById(user1Id).get();
        user1.setPoints(20);
        userDao.save(user1);
        OrderEntity order = OrderEntity.builder()
                .userId(user1Id)
                .trainId(train1Id)
                .departureStationId(station1Id)
                .arrivalStationId(station2Id)
                .status(OrderStatus.PAID)
                .seat("1车1座")
                .price(BigDecimal.valueOf(100))
                .points(20)
                .build();
        orderDao.save(order);

        int beforePoints = user1.getPoints();

        orderService.cancelOrder(order.getId());

        int afterPoints = userDao.findById(user1Id).get().getPoints();
        order = orderDao.findById(order.getId()).get();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        //assertEquals(0, afterPoints);
    }

    /**
     * 测试取消订单用例的正常流程，取消未支付的订单
     */
    @Test
    void cancelOrderTest2(){
        UserEntity user1 = userDao.findById(user1Id).get();
        OrderEntity order = OrderEntity.builder()
                .userId(user1Id)
                .trainId(train1Id)
                .departureStationId(station1Id)
                .arrivalStationId(station2Id)
                .status(OrderStatus.PENDING_PAYMENT)
                .seat("1车1座")
                .price(BigDecimal.valueOf(100))
                .points(20)
                .build();
        orderDao.save(order);

        int beforePoints = user1.getPoints();

        orderService.cancelOrder(order.getId());

        int afterPoints = userDao.findById(user1Id).get().getPoints();
        order = orderDao.findById(order.getId()).get();

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(afterPoints, beforePoints);
    }

    /**
     * 测试取消订单用例是否抛出非法订单状态异常
     */
    @Test
    void cancelOrderTest3(){
        final OrderEntity order = OrderEntity.builder()
                .userId(user1Id)
                .trainId(train1Id)
                .departureStationId(station1Id)
                .arrivalStationId(station2Id)
                .status(OrderStatus.COMPLETED)
                .seat("1车1座")
                .price(BigDecimal.valueOf(100))
                .points(20)
                .build();
        orderDao.save(order);

        BizException exception = assertThrows(BizException.class, () -> orderService.cancelOrder(order.getId()));
        assertEquals(BizError.ILLEAGAL_ORDER_STATUS.getCode(), exception.getCode());
    }

    /**
     * 测试取消订单用例是否抛出非法订单状态异常
     */
    @Test
    void cancelOrderTest4(){
        final OrderEntity order = OrderEntity.builder()
                .userId(user1Id)
                .trainId(train1Id)
                .departureStationId(station1Id)
                .arrivalStationId(station2Id)
                .status(OrderStatus.CANCELLED)
                .seat("1车1座")
                .price(BigDecimal.valueOf(100))
                .points(20)
                .build();
        orderDao.save(order);

        BizException exception = assertThrows(BizException.class, () -> orderService.cancelOrder(order.getId()));
        assertEquals(BizError.ILLEAGAL_ORDER_STATUS.getCode(), exception.getCode());
    }
}
