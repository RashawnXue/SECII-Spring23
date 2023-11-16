package org.fffd.l23o6.exception;

import io.github.lyc8503.spring.starter.incantation.exception.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BizError implements ErrorType {

    USERNAME_EXISTS(200001, "用户名已存在", 400),
    INVALID_CREDENTIAL(200002, "用户名或密码错误", 400),
    STATIONNAME_EXISTS(200003, "同名站点已存在", 400),
    ROUTENAME_EXISTS(200004, "同名路线已存在", 400),
    OUT_OF_SEAT(300001, "无可用座位", 400),
    ILLEAGAL_ORDER_STATUS(400001, "非法的订单状态", 400),
    ILLEAGAL_PAYMENT_TYPE(400002, "非法的支付方式", 400),
    WALLET_BALANCE_NOT_ENOUGH(400003, "钱包余额不足", 400),
    ROUTE_DATA_DEPENDENCY(500001, "存在路线数据依赖", 400),
    TRAIN_DATA_DEPENDENCY(500002, "存在车次数据依赖", 400),
    ILLEAGAL_OPERATION(600001, "非法操作，用户权限不足", 400);

    final int code;
    final String message;
    final int httpCode;
}
