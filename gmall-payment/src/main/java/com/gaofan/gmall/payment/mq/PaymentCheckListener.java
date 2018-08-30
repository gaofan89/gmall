package com.gaofan.gmall.payment.mq;

import com.gaofan.gmall.payment.service.PaymentService;
import com.gaofan.gmall.util.ActiveMQUtil;
import com.gaofan.gmall.util.CommonUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Map;

@Component
public class PaymentCheckListener {

    @Autowired
    ActiveMQUtil activeMQUtil;

    @Autowired
    PaymentService paymentService;

    @JmsListener(containerFactory ="jmsQueueListener" ,destination = CommonUtil.PAYMENT_CHECK_MQ)
    public void consumerCheckPay(ActiveMQMapMessage message) throws JMSException {
        String outTradeNo = message.getString("outTradeNo");
        int count = message.getInt("count");
        System.out.println("监听到延迟检查队列，执行延迟检查第" + (6 - count) + "次检查");

        Map<String,String> ispay = paymentService.checkPayment(outTradeNo);
        String trade_no = ispay.get("tradeNo");
        String queryString = ispay.get("queryString");
        if(ispay.get("statu").equals("TRADE_CLOSED") || ispay.get("statu").equals("TRADE_SUCCESS")){
            //幂等性检查
            boolean isPaied = paymentService.checkPaied(outTradeNo);
            if(isPaied){
                paymentService.sendSuccessPayMq(outTradeNo,trade_no,queryString);
            }
        }else{
            //未支付，继续发送延迟消息队列
            if(count > 0){
                paymentService.sendCheckPay(outTradeNo,count-1);
            }else{
                System.out.println("延迟消息队列【"+CommonUtil.PAYMENT_CHECK_MQ+"】,发送完毕，支付流水号【"+outTradeNo+"】未确认是否支付成功");
            }
        }



    }
}
