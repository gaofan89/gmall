package com.gaofan.gmall.payment.service;

import com.gaofan.gmall.bean.OrderInfo;
import com.gaofan.gmall.bean.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    void sendSuccessPayMq(String outTradeNo, String trackingNo,String queryString);

    void sendCheckPay(String outTradeNo, int count);

    void savePaymentInfo(PaymentInfo paymentInfo);

    PaymentInfo getByOutTradeNo(String out_trade_no);

    void updatePaymentInfo(PaymentInfo paymentInfo);

    boolean checkPaied(String out_trade_no);

    Map<String,String> checkPayment(String outTradeNo);
}
