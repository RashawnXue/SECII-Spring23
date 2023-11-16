package org.fffd.l23o6.util.strategy.payment;

import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.exception.BizError;

import java.math.BigDecimal;

public class Wallet {
    private static Wallet instance = new Wallet();
    private BigDecimal money;

    public Wallet() {
        money = new BigDecimal(100);
    }

    public static Wallet getInstance() {
        return instance;
    }

    /**
     * 支付接口
     * @param payAmount 支付金额
     * @return 钱包余额
     */
    public BigDecimal pay(BigDecimal payAmount) {
        if (money.compareTo(payAmount) < 0) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS, "支付失败:余额不足 当前余额:" + money);
        }
        money = money.subtract(payAmount);
        return money;
    }

    /**
     * 充值接口
     * @param depositAmount 充值金额
     * @return 余额
     */
    public BigDecimal deposit(BigDecimal depositAmount) {
        money = money.add(depositAmount);
        return money;
    }

}
