package com.gaofan.gmall.payment.service;

public interface PaymentService {
    void sendSuccessPayMq(String outTradeNo, String trackingNo);

    void sendCheckPay(String outTradeNo, int count);
}
