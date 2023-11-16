package org.fffd.l23o6.util.strategy.points;

import java.math.BigDecimal;

public abstract class PointsStrategy {
    /**
     * 根据订单原价和用户持有积分计算实际价格
     * @param basePrice 订单原价
     * @param points 用户持有积分
     * @return 抵扣后订单价格
     */
    public abstract BigDecimal calcOrderPrice(BigDecimal basePrice, int points);

    /**
     * 根据用户持有的积分计算一次支付时抵扣所消耗的积分
     * @param points 用户持有积分
     * @return 支付时抵扣所消耗的积分
     */
    public abstract int calcConsumedPoints(int points);

    /**
     *根据订单原价计算奖励积分
     * @param price 订单原价
     * @return 奖励积分
     */
    public abstract int calcBonusPoints(BigDecimal price);
}
