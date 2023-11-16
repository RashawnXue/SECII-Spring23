package org.fffd.l23o6.task;

import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.dao.OrderDao;
import org.fffd.l23o6.dao.RouteDao;
import org.fffd.l23o6.dao.TrainDao;
import org.fffd.l23o6.dao.UserDao;
import org.fffd.l23o6.pojo.entity.OrderEntity;
import org.fffd.l23o6.pojo.entity.RouteEntity;
import org.fffd.l23o6.pojo.entity.TrainEntity;
import org.fffd.l23o6.pojo.entity.UserEntity;
import org.fffd.l23o6.pojo.enum_.OrderStatus;
import org.fffd.l23o6.util.strategy.points.MileagePointsStrategy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTask {
    private final UserDao userDao;
    private final OrderDao orderDao;
    private final TrainDao trainDao;
    private final RouteDao routeDao;

    /**
     * 定时任务，每分钟检查一次订单列表，若对应列车已发车，则转换订单状态，同时对于已完成的订单奖励用户积分
     */
    @Scheduled(fixedRate = 60000)
    public void updateOrderStatus(){
        List<OrderEntity> orders = orderDao.findAll();
        Date now = new Date();
        for(OrderEntity order : orders){
            if(order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.COMPLETED) {
                continue;
            }
            TrainEntity train = trainDao.findById(order.getTrainId()).get();
            RouteEntity route = routeDao.findById(train.getRouteId()).get();
            int startIndex = route.getStationIds().indexOf(order.getDepartureStationId());
            Date departureTime = train.getDepartureTimes().get(startIndex);
            if(now.after(departureTime)){
                if(order.getStatus() == OrderStatus.PAID){
                    // 奖励用户积分
                    int bonusPoints = MileagePointsStrategy.INSTANCE.calcBonusPoints(order.getPrice());
                    UserEntity user = userDao.findById(order.getUserId()).get();
                    user.setPoints(user.getPoints() + bonusPoints);
                    userDao.save(user);
                    order.setStatus(OrderStatus.COMPLETED);
                } else {
                    order.setStatus(OrderStatus.CANCELLED);
                }
                orderDao.save(order);
            }
        }
    }
}
