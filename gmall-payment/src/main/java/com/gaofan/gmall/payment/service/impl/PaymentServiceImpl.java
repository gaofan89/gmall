package com.gaofan.gmall.payment.service.impl;

import com.gaofan.gmall.payment.service.PaymentService;
import com.gaofan.gmall.util.ActiveMQUtil;
import com.gaofan.gmall.util.CommonUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Override
    public void sendSuccessPayMq(String outTradeNo, String trackingNo) {
        try{

            Connection connection = activeMQUtil.getConnection();
            connection.start();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue(CommonUtil.PAYMENT_SUCCESS_MQ);

            MessageProducer producer = session.createProducer(queue);

            ActiveMQMapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("outTradeNo",outTradeNo);
            mapMessage.setString("trackingNo",trackingNo);
            mapMessage.setLongProperty("",5);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);

            session.commit();
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("支付成功，发送支付服务的消息队列。。");
    }

    @Override
    public void sendCheckPay(String outTradeNo, int count) {
        try{

            Connection connection = activeMQUtil.getConnection();
            connection.start();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue(CommonUtil.PAYMENT_SUCCESS_MQ);

            MessageProducer producer = session.createProducer(queue);

            ActiveMQMapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("outTradeNo",outTradeNo);
            //mapMessage.setString("trackingNo",trackingNo);
            mapMessage.setLongProperty("",5);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);

            session.commit();
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("支付成功，发送支付服务的消息队列。。");
    }
}
