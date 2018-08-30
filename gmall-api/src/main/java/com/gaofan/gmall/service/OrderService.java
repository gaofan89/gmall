package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.OrderInfo;

public interface OrderService {
    void updatePaySuccess(OrderInfo orderInfo);

    void sendDeliveryMq(String outTradeNo);

    String gentradeCode(String userId);

    boolean checkTradeCode(String tradeCode,String userId);

    String saveOrder(OrderInfo orderInfo);

    OrderInfo getById(String orderId);

    OrderInfo getByOutTradeNo(String out_trade_no);
}
