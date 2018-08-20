package com.gaofan.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.gaofan.gmall.bean.OrderInfo;
import com.gaofan.gmall.payment.config.AlipayConfig;
import com.gaofan.gmall.payment.service.PaymentService;
import com.gaofan.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class PaymentController {

    @Reference
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    AlipayClient alipayClient;

    @RequestMapping("/alipay/callback/return")
    public String callBackReturn(){

        return "paySuccessTest";
    }

    @RequestMapping("/alipay/submit")
    @ResponseBody
    public String alipay(String orderId){
        OrderInfo orderById = orderService.getById(orderId);

        // 重定向到支付宝平台
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("out_trade_no",orderById.getOutTradeNo());
        stringObjectHashMap.put("product_code","FAST_INSTANT_TRADE_PAY");
        stringObjectHashMap.put("total_amount",orderById.getTotalAmount());
        stringObjectHashMap.put("subject","测试硅谷手机phone");

        String json = JSON.toJSONString(stringObjectHashMap);
        alipayRequest.setBizContent(json);

        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return form;
    }
    @RequestMapping("/mx/submit")
    public String mx(){
        //重定向微信平台

        return "";
    }

    @RequestMapping("index")
    public String index(String orderId, ModelMap map){

        OrderInfo orderInfo = orderService.getById(orderId);

        map.put("totalAmount",orderInfo.getTotalAmount());
        map.put("orderId",orderId);
        map.put("outTradeNo",orderInfo.getOutTradeNo());

        return "index";
    }
    //String outTradeNo,String trackingNo
    //paymentService.sendSuccessPayMq(outTradeNo,trackingNo);
    //
    //        paymentService.sendCheckPay(outTradeNo,5);
}
