package com.gaofan.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.gaofan.gmall.bean.PaymentInfo;
import com.gaofan.gmall.payment.mapper.PaymentInfoMapper;
import com.gaofan.gmall.payment.service.PaymentService;
import com.gaofan.gmall.util.ActiveMQUtil;
import com.gaofan.gmall.util.CommonUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Autowired
    AlipayClient alipayClient;

    @Override
    public void sendSuccessPayMq(String outTradeNo, String trackingNo,String queryString) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(outTradeNo);
        paymentInfo.setPaymentStatus("已支付");
        paymentInfo.setAlipayTradeNo(trackingNo);
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setCallbackContent(queryString);
        updatePaymentInfo(paymentInfo);
        try{

            Connection connection = activeMQUtil.getConnection();
            connection.start();

            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue(CommonUtil.PAYMENT_SUCCESS_MQ);

            MessageProducer producer = session.createProducer(queue);

            ActiveMQMapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("outTradeNo",outTradeNo);
            mapMessage.setString("trackingNo",trackingNo);

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
            Queue queue = session.createQueue(CommonUtil.PAYMENT_CHECK_MQ);

            MessageProducer producer = session.createProducer(queue);

            ActiveMQMapMessage mapMessage = new ActiveMQMapMessage();
            mapMessage.setString("outTradeNo",outTradeNo);
            mapMessage.setInt("count",count);
            //mapMessage.setString("trackingNo",trackingNo);
            mapMessage.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY,30*1000);

            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(mapMessage);

            session.commit();
            connection.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("发送第"+(6-count)+"次的消息队列。。");
    }

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {

        paymentInfoMapper.insertSelective(paymentInfo);

    }

    @Override
    public PaymentInfo getByOutTradeNo(String out_trade_no) {
        Example example = new Example(PaymentInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("outTradeNo",out_trade_no);
        return paymentInfoMapper.selectOneByExample(example);
    }

    @Override
    public void updatePaymentInfo(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("outTradeNo",paymentInfo.getOutTradeNo());
        paymentInfoMapper.updateByExampleSelective(paymentInfo,example);
    }

    @Override
    public boolean checkPaied(String out_trade_no) {
        boolean flag = false;
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOutTradeNo(out_trade_no);
        PaymentInfo paymentInfo1 = paymentInfoMapper.selectOne(paymentInfo);

        if(paymentInfo1 != null && "已支付".equals(paymentInfo1.getPaymentStatus())){

            flag = true;
        }

        return false;
    }

    @Override
    public Map<String,String> checkPayment(String outTradeNo) {
        Map<String,String> rst = new HashMap<>();
        rst.put("statu","fail");
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            Map<String,String> param = new HashMap<>();

            param.put("out_trade_no",outTradeNo);
            //param.put("trade_no",outTradeNo);
            String paramStr = JSON.toJSONString(param);
            request.setBizContent(paramStr);
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response.isSuccess()) {
                rst.put("tradeNo",response.getTradeNo());
                rst.put("statu",response.getTradeStatus());
                rst.put("queryString",response.getBody());
            } else {
                System.out.println("用户未扫码");
            }
        }catch (Exception e){

        }

        return rst;
    }
}
