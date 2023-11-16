package org.fffd.l23o6.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.alipay.api.AlipayApiException;
import io.github.lyc8503.spring.starter.incantation.pojo.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.CreateOrderRequest;
import org.fffd.l23o6.pojo.vo.order.OrderIdVO;
import org.fffd.l23o6.pojo.vo.order.OrderVO;
import org.fffd.l23o6.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/v1/")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    /**
     * 创建订单
     * @param request 创建订单request
     * @return OrderIdVO
     */
    @PostMapping("order")
    public CommonResponse<OrderIdVO> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        StpUtil.checkLogin();
        return CommonResponse.success(new OrderIdVO(orderService.createOrder(StpUtil.getLoginIdAsString(), request.getTrainId(), request.getStartStationId(), request.getEndStationId(), request.getSeatType(), null)));
    }

    /**
     * 获取用户订单列表
     * @return 用户订单列表
     */
    @GetMapping("order")
    public CommonResponse<List<OrderVO>> listOrders(){
        StpUtil.checkLogin();
        return CommonResponse.success(orderService.listOrders(StpUtil.getLoginIdAsString()));
    }

    /**
     * 获取订单
     * @param orderId 订单id
     * @return 对应的OrderVO
     */
    @GetMapping("order/{orderId}")
    public CommonResponse<OrderVO> getOrder(@PathVariable("orderId") Long orderId) {
        return CommonResponse.success(orderService.getOrder(orderId));
    }

    /**
     * 支付订单，不实际产生支付操作，返回支付跳转的页面
     *
     * @param orderId 订单Id
     * @param type 列车种类
     * @param points 产生积分
     * @return 成功响应
     */
    @GetMapping(value = "order/{orderId}/pay")
    public CommonResponse<String> payOrder(@PathVariable("orderId") Long orderId, @Valid @RequestParam(value = "type") String type, @RequestParam(value = "points") Boolean points) throws AlipayApiException {
        StpUtil.checkLogin();
        PaymentType paymentType = null;
        if ("扫码支付".equals(type)) {
            paymentType = PaymentType.SCAN_QRCODE;
        } else {
            paymentType = PaymentType.WALLET_PAY;
        }
        String result = orderService.payOrder(orderId, paymentType, points);
        return CommonResponse.success(result);
    }

    /**
     * 支付宝异步回调，获取订单支付状态
     * @return success响应
     */
    @PostMapping(value = "order/notify")
    public CommonResponse<?> updateOrder(HttpServletRequest request) {
        orderService.updateOrder(request);
        return CommonResponse.success();
    }

    /**
     * 取消订单
     * @param orderId 订单id
     * @return success响应
     */
    @PatchMapping("order/{orderId}/cancel")
    public CommonResponse<?> cancelOrder(@PathVariable("orderId") Long orderId) {
        StpUtil.checkLogin();
        orderService.cancelOrder(orderId);
        return CommonResponse.success();
    }
}