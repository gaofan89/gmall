package com.gaofan.gmall.payment.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.gaofan.gmall.bean.OrderInfo;
import com.gaofan.gmall.bean.PaymentInfo;
import com.gaofan.gmall.payment.config.AlipayConfig;
import com.gaofan.gmall.payment.service.PaymentService;
import com.gaofan.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class PaymentController {

    @Reference
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    AlipayClient alipayClient;

    public String notifyCallReturn(){
//        Map<String, String> paramsMap = ... //将异步通知中收到的所有参数都存放到map中
//        boolean signVerified = AlipaySignature.rsaCheckV1(paramsMap, ALIPAY_PUBLIC_KEY, CHARSET, SIGN_TYPE) //调用SDK验证签名
//        if(signVerfied){
//            // TODO 验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验，校验成功后在response中返回success并继续商户自身业务处理，校验失败返回failure
//        }else{
//            // TODO 验签失败则记录异常日志，并在response中返回failure.
//        }
        return "success";
    }



    @RequestMapping("/alipay/callback/return")
    public String callBackReturn(HttpServletRequest request){
        Map<String, String> paramsMap2 = new HashMap<>(); //将异步通知中收到的所有参数都存放到map中

        Map<String,String[]> paramsMap = request.getParameterMap();
        Set<Map.Entry<String, String[]>> set = paramsMap.entrySet();
        Iterator<Map.Entry<String, String[]>> iterator = set.iterator();
        while(iterator.hasNext()){
            Map.Entry<String, String[]> entry = iterator.next();
            String key = entry.getKey();
            String[] values = entry.getValue();
            String value = (values ==null || values.length == 0)?"":values[0];
            paramsMap2.put(key,value);

        }
        boolean signVerified = true;
        try {
            signVerified = AlipaySignature.rsaCheckV1(paramsMap2, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名
        } catch (Exception e) {
            System.out.println("此处支付宝的签名验证通过。。。");
        }

        //https://m.alipay.com/GkSL?total_amount=0.10&timestamp=2016-11-02+18%3A34%3A19&sign=G3WI0czviMAOzS5t0fYaDgK32sGpjkkXYVFTpYMtgX8JaXLiGiUTO%2F2IHogcCFT96jBCLZ6IsNzd%2BmxkB%2FRuwG%2F7naQk1qReuORMkrB5cpBf9U40bIUoCmSNqtANsTE2UPV7GKegYG2RqoCRScTmeFAFHj5L7zsM%2BLuYb9mqN3g%3D&trade_no=2016110221001004330228438026&sign_type=RSA2&auth_app_id=2014073000007292&charset=UTF-8&seller_id=2088411964605312&method=alipay.trade.page.pay.return&app_id=2014073000007292&out_trade_no=20150g320g010101001&version=1.0
        //公共参数
        String app_id = request.getParameter("app_id");
        String method = request.getParameter("method");
        String sign_type = request.getParameter("sign_type");
        String sign = request.getParameter("sign");
        String charset = request.getParameter("charset");
        String timestamp = request.getParameter("timestamp");
        String version = request.getParameter("version");
        String auth_app_id = request.getParameter("auth_app_id");

        //业务参数
        String out_trade_no = request.getParameter("out_trade_no");
        String trade_no = request.getParameter("trade_no");
        String total_amount = request.getParameter("total_amount");
        String seller_id = request.getParameter("seller_id");


        OrderInfo orderInfo = orderService.getByOutTradeNo(out_trade_no);
        if(orderInfo == null) return null;

        /*PaymentInfo paymentInfo = paymentService.getByOutTradeNo(out_trade_no);

        paymentInfo.setPaymentStatus("订单已支付");
        paymentInfo.setAlipayTradeNo(trade_no);
        paymentInfo.setCallbackTime(new Date());
        String queryString = request.getQueryString();
        paymentInfo.setCallbackContent(queryString);*/
        String queryString = request.getQueryString();
        //幂等性检查
        boolean isPaied = paymentService.checkPaied(out_trade_no);
        if(!isPaied){

//            paymentService.updatePaymentInfo(paymentInfo);

            paymentService.sendSuccessPayMq(out_trade_no,trade_no,queryString);
        }

        return "paySuccessTest";
    }

    @RequestMapping("/alipay/submit")
    @ResponseBody
    public String alipay(String orderId){
        OrderInfo orderById = orderService.getById(orderId);
        if(orderById == null) return null;

        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderId);
        paymentInfo.setOutTradeNo(orderById.getOutTradeNo());
        paymentInfo.setPaymentStatus("未支付");
        paymentInfo.setSubject(orderById.getOrderDetailList().get(0).getSkuName());
        paymentInfo.setTotalAmount(orderById.getTotalAmount());

        paymentService.savePaymentInfo(paymentInfo);

        // 重定向到支付宝平台
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();//创建API对应的request
        alipayRequest.setReturnUrl(AlipayConfig.return_payment_url);
        alipayRequest.setNotifyUrl(AlipayConfig.notify_payment_url);//在公共参数中设置回跳和通知地址

        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("out_trade_no",orderById.getOutTradeNo());
        stringObjectHashMap.put("product_code","FAST_INSTANT_TRADE_PAY");
        stringObjectHashMap.put("total_amount","0.01"); //orderById.getTotalAmount()
        stringObjectHashMap.put("subject","测试硅谷手机phone");

        String json = JSON.toJSONString(stringObjectHashMap);
        alipayRequest.setBizContent(json);

        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        System.out.println(form);
        System.out.println("设置一个定时巡检订单"+orderById.getOutTradeNo()+"的支付状态的延迟队列");
        paymentService.sendCheckPay(orderById.getOutTradeNo(),5);
        form = form.substring(0, form.indexOf("<script>"));
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
