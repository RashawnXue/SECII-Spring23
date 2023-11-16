package org.fffd.l23o6.util.strategy.payment;

import com.alipay.api.AlipayApiException;
import org.fffd.l23o6.pojo.entity.OrderEntity;

public interface PaymentStrategy {
    /**
     * 支付订单
     * @param order 订单实体
     * @return
     * @throws AlipayApiException
     */
    String payOrder(OrderEntity order) throws AlipayApiException;

    /**
     * 更新order状态
     *
     * @param order
     * @return
     */
    boolean updateOrder(OrderEntity order);
}
