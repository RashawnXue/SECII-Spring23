package org.fffd.l23o6.util.strategy.payment;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import io.github.lyc8503.spring.starter.incantation.exception.BizException;
import org.fffd.l23o6.config.AlipayConfig;
import org.fffd.l23o6.exception.BizError;
import org.fffd.l23o6.pojo.entity.OrderEntity;

public class AlipayStrategy implements PaymentStrategy{
    private AlipayConfig config = new AlipayConfig();

    @Override
    public String payOrder(OrderEntity order) throws AlipayApiException {
        if (order == null) {
            throw new BizException(BizError.ILLEAGAL_ORDER_STATUS, "订单不存在");
        }
        AlipayClient alipayClient = new DefaultAlipayClient(config.getGatewayUrl(), config.getAppId(), config.getAlipayPrivateKey(), "json", config.getCharset(), config.getAlipayPublicKey(), config.getSignType());
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        String out_trade_no = String.valueOf(order.getId());
        //付款金额，必填
        String total_amount = String.valueOf(order.getPrice());
        //订单名称，必填
        String subject = "订单"+order.getId();
        alipayRequest.setReturnUrl(config.getReturnUrl()+"order/"+order.getId());
        alipayRequest.setNotifyUrl(config.getNotifyUrl());
        alipayRequest.setBizContent("{\"out_trade_no\":\""+ out_trade_no +"\","
                + "\"total_amount\":\""+ total_amount +"\","
                + "\"subject\":\""+ subject +"\","
                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        AlipayTradePagePayResponse response = alipayClient.pageExecute(alipayRequest);
        String result = response.getBody();
        System.out.println(result);
        return result;
    }

    @Override
    public boolean updateOrder(OrderEntity order) {
        return true;
    }

}
