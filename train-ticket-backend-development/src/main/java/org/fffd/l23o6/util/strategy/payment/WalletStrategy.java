package org.fffd.l23o6.util.strategy.payment;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;

import java.math.BigDecimal;


public class WalletStrategy implements PaymentStrategy{
    @Override
    public String payOrder(OrderEntity order) {
        return "";
    }

    @Override
    public boolean updateOrder(OrderEntity order) {
        BigDecimal money = Wallet.getInstance().pay(order.getPrice());
        return money.compareTo(BigDecimal.valueOf(0)) >= 0;
    }

}
