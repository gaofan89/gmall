package com.gaofan.gmall.order.mq;

import com.gaofan.gmall.bean.OrderInfo;
import com.gaofan.gmall.service.OrderService;
import com.gaofan.gmall.util.ActiveMQUtil;
import com.gaofan.gmall.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.MapMessage;

@Component
public class OrderMqListener {

    @Autowired
    private OrderService orderService;

    @JmsListener(containerFactory = "jmsQueueListener",destination =CommonUtil.PAYMENT_SUCCESS_MQ)
    public void listenerPaySuccess(MapMessage mapMessage) throws JMSException {
        String outTradeNo = mapMessage.getString("outTradeNo");
        String trackingNo = mapMessage.getString("trackingNo");

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setTrackingNo(trackingNo);
        orderInfo.setOrderStatus("已支付");
        orderInfo.setProcessStatus("出库中");
        orderService.updatePaySuccess(orderInfo);

        orderService.sendDeliveryMq(outTradeNo); //发送出库消息

        System.out.println("订单监听支付成功的监听器。。。trackingNo:"+trackingNo+", outTradeNo:"+outTradeNo);
    }

}
