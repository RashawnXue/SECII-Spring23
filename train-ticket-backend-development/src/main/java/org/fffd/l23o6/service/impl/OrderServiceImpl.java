package org.fffd.l23o6.service.impl;

import com.alipay.api.AlipayApiException;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.service.OrderService;
import org.fffd.l23o6.service.UserService;
import org.fffd.l23o6.util.context.train.TrainSeatContext;
import org.fffd.l23o6.util.strategy.payment.AlipayStrategy;
import org.fffd.l23o6.util.strategy.payment.PaymentStrategy;
import org.fffd.l23o6.util.strategy.payment.Wallet;
import org.fffd.l23o6.util.strategy.payment.WalletStrategy;
import org.fffd.l23o6.util.strategy.points.MileagePointsStrategy;
import org.fffd.l23o6.util.strategy.train.TrainSeatStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final UserService userService;
    private final OrderDao orderDao;
    private final UserDao userDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;
    /**
     * 支付所选取的策略
     */
    private PaymentStrategy paymentStrategy;

    /**
     * 支付宝的支付状态码列表
     */
    private final String[] alipayTradeStatus = {"WAIT_BUYER_PAY", "TRADE_SUCCESS", "TRADE_CLOSED", "TRADE_FINISHED"};

    public Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType,
            Long seatNumber) {
        Long userId = userDao.findByUsername(username).getId();
        TrainEntity train = trainDao.findById(trainId).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();

        int startStationIndex = route.getStationIds().indexOf(fromStationId);
        int endStationIndex = route.getStationIds().indexOf(toStationId);
        String seat = null;
        BigDecimal price = null;

        // 使用对应策略分配车座和价格
        TrainSeatStrategy seatStrategy = TrainSeatContext.getInstance().getStrategy(train.getTrainType());
        seat = seatStrategy.allocSeatAndUpdateSeatMap(startStationIndex, endStationIndex,
                seatStrategy.fromString(seatType), train.getSeats());
        price = BigDecimal.valueOf(seatStrategy.calcPrice(startStationIndex, endStationIndex,
                seatStrategy.fromString(seatType)));

        if (seat == null) {
            throw new BizException(BizError.OUT_OF_SEAT);
        }

        // 保存订单
        OrderEntity order = OrderEntity.builder().trainId(trainId).userId(userId).seat(seat).price(price).points(0)
                .status(OrderStatus.PENDING_PAYMENT).arrivalStationId(toStationId).departureStationId(fromStationId)
                .build();
        orderDao.save(order);

        // 修改车次座位表并保存
        train.setUpdatedAt(null);// 强制数据库更新
        trainDao.save(train);

        return order.getId();
    }

    public List<OrderVO> listOrders(String username) {
        Long userId = userDao.findByUsername(username).getId();
        List<OrderEntity> orders = orderDao.findByUserId(userId);
        orders.sort((o1,o2)-> o2.getId().compareTo(o1.getId()));
        return orders.stream().map(order -> {
            TrainEntity train = trainDao.findById(order.getTrainId()).get();
            RouteEntity route = routeDao.findById(train.getRouteId()).get();
            int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
            int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
            return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                    .seat(order.getSeat()).status(order.getStatus().getText())
                    .price(order.getPrice())
                    .createdAt(order.getCreatedAt())
                    .startStationId(order.getDepartureStationId())
                    .endStationId(order.getArrivalStationId())
                    .departureTime(train.getDepartureTimes().get(startIndex))
                    .arrivalTime(train.getArrivalTimes().get(endIndex))
                    .build();
        }).collect(Collectors.toList());
    }

    public OrderVO getOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();
        int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        return OrderVO.builder().id(order.getId()).trainId(order.getTrainId())
                .seat(order.getSeat()).status(order.getStatus().getText())
                .price(order.getPrice())
                .createdAt(order.getCreatedAt())
                .startStationId(order.getDepartureStationId())
                .endStationId(order.getArrivalStationId())
                .departureTime(train.getDepartureTimes().get(startIndex))
                .arrivalTime(train.getArrivalTimes().get(endIndex))
                .build();
    }

    public void cancelOrder(Long id) {
        OrderEntity order = orderDao.findById(id).get();
        TrainEntity train = trainDao.findById(order.getTrainId()).get();
        RouteEntity route = routeDao.findById(train.getRouteId()).get();

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        // 退款和恢复积分
        if (order.getStatus() == OrderStatus.PAID){
            // 退款
            BigDecimal price = order.getPrice();
            Wallet.getInstance().deposit(price);
            // 恢复积分
            int points = order.getPoints();
            userService.addPoints(order.getUserId(), -points);
        }

        // 归还车座
        boolean[][] newSeatMap;
        int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
        int endIndex = route.getStationIds().indexOf(order.getArrivalStationId());
        TrainSeatStrategy seatStrategy = TrainSeatContext.getInstance().getStrategy(train.getTrainType());
        newSeatMap = seatStrategy.getRevokedSeatMap(startIndex, endIndex, train.getSeats(), order.getSeat());
        train.setSeats(newSeatMap);
        train.setUpdatedAt(null);// 强制数据库更新
        trainDao.save(train);

        // 修改订单状态
        order.setStatus(OrderStatus.CANCELLED);
        orderDao.save(order);
    }

    public String payOrder(Long id, PaymentType type, Boolean usePoints) throws AlipayApiException {
        OrderEntity order = orderDao.findById(id).get();

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }

        UserEntity user = userDao.findById(order.getUserId()).get();
        BigDecimal price = order.getPrice();
        int points = user.getPoints();

        // 使用积分抵扣金额
        if(usePoints){
            price = MileagePointsStrategy.INSTANCE.calcOrderPrice(price, points);
            order.setPrice(price);
            int consumedPoints = MileagePointsStrategy.INSTANCE.calcConsumedPoints(points);
            order.setPoints(-consumedPoints);
            orderDao.save(order);
        }

        // 支付策略
        switch (type){
            case SCAN_QRCODE:
                // 二维码支付（支付宝沙箱环境）
                setPaymentStrategy(new AlipayStrategy());
                break;
            case WALLET_PAY:
                // 模拟钱包支付（桩程序）
                setPaymentStrategy(new WalletStrategy());
                break;
            default:
                throw new BizException(BizError.ILLEAGAL_PAYMENT_TYPE);
        }

        return paymentStrategy.payOrder(order);
    }

    @Override
    public void updateOrder(HttpServletRequest request) {
        Long orderId = Long.valueOf(request.getParameter("out_trade_no"));
        OrderEntity order = orderDao.findById(orderId).get();
        String status = getPaymentStatus(request.getParameter("trade_status"));
        UserEntity user = userDao.findById(order.getUserId()).get();

        // 检查order的状态，order应该为待支付
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }
        if (OrderStatus.PAID.getText().equals(status)) {
            // 若当前request返回状态已支付，则支付成功
            // 根据不同策略进行操作判断能否切换状态
            if (paymentStrategy.updateOrder(order)) {
                // 支付成功则减少用户对应积分，更新order状态
                user = userService.addPoints(user.getId(), order.getPoints());
                userDao.save(user);
                order.setStatus(OrderStatus.PAID);
                orderDao.save(order);
            }
        }
    }

    private void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    /**
     * 通过表驱动，将request中的状态转换为OrderStatus的状态
     * @param status request中获取到的状态
     * @return OrderStatus的状态
     */
    private String getPaymentStatus(String status) {
        int index = -1;
        for (int i = 0 ; i < alipayTradeStatus.length ; ++i) {
            if (alipayTradeStatus[i].equals(status)) {
                index = i;
            }
        }
        OrderStatus[] enumValues = OrderStatus.class.getEnumConstants();
        if (index < 0 || index >= enumValues.length) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS);
        }
        return enumValues[index].getText();
    }
}
