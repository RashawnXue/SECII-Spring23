package org.fffd.l23o6.service;

import java.util.List;

import com.alipay.api.AlipayApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.fffd.l23o6.pojo.enum_.PaymentType;
import org.fffd.l23o6.pojo.vo.order.OrderVO;

public interface OrderService {
    /**
     * 根据用户及车票信息创建订单
     * @param username 用户名
     * @param trainId 车次ID
     * @param fromStationId 出发站ID
     * @param toStationId 目的站ID
     * @param seatType 座位类型
     * @param seatNumber 座位数量
     * @return 创建订单的ID
     */
    Long createOrder(String username, Long trainId, Long fromStationId, Long toStationId, String seatType, Long seatNumber);

    /**
     * 列出用户的所有订单
     * @param username 用户名
     * @return 用户的订单列表
     */
    List<OrderVO> listOrders(String username);

    /**
     * 得到某一订单的详细信息
     * @param id 订单ID
     * @return 订单信息
     */
    OrderVO getOrder(Long id);

    /**
     * 取消订单
     * @param id 订单ID
     */
    void cancelOrder(Long id);

    /**
     * 支付订单
     * @param id 订单ID
     * @param type 支付方式
     * @param usePoints 是否使用积分抵扣
     * @return 涉及支付功能的HTML代码
     * @throws AlipayApiException
     */
    String payOrder(Long id, PaymentType type, Boolean usePoints) throws AlipayApiException;

    /**
     * 根据request更新订单信息
     * @param request request请求
     */
    void updateOrder(HttpServletRequest request);
}
