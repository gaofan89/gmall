package com.gaofan.gmall.order.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gaofan.gmall.annotation.LoginRequire;
import com.gaofan.gmall.bean.CartInfo;
import com.gaofan.gmall.bean.OrderDetail;
import com.gaofan.gmall.bean.OrderInfo;
import com.gaofan.gmall.bean.UserAddress;
import com.gaofan.gmall.bean.enums.PaymentWay;
import com.gaofan.gmall.service.CartService;
import com.gaofan.gmall.service.OrderService;
import com.gaofan.gmall.service.SkuInfoService;
import com.gaofan.gmall.service.UserService;
import com.gaofan.gmall.util.CommonUtil;
import com.gaofan.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.number.OrderingComparison;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;

@Controller
public class OrderController {

    @Reference
    CartService cartService;

    @Reference
    UserService userService;

    @Reference
    OrderService orderService;

    @Reference
    private SkuInfoService skuInfoService;


    @RequestMapping("submitOrder")
    @LoginRequire(requiredLogin = true)
    public String submitOrder(HttpServletRequest request,String tradeCode,ModelMap map){
        String userId = (String)request.getAttribute("userId");
        boolean isCanSub = orderService.checkTradeCode(tradeCode,userId);
        if(!isCanSub){
            map.put("errMsg","获取订单失败");
            return "tradeFail";
        }else{
            List<CartInfo> cartInfos = cartService.getCartCacheByChecked(userId);
            OrderInfo orderInfo = new OrderInfo();
            List<OrderDetail> orderDetails = new ArrayList<>();
            if(cartInfos == null || cartInfos.size() ==0){
                map.put("errMsg","请选择需要购买的商品再提交订单");
                return "tradeFail";
            }
            for (CartInfo cartInfo : cartInfos) {
                //验价格
                boolean bprice = skuInfoService.checkPrice(cartInfo);
                if(!bprice){
                    map.put("errMsg","订单中商品的价格或库存发现变化，请重新确定并提交订单");
                    return "tradeFail";
                }
                //验库存 待完成
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                orderDetails.add(orderDetail);
            }
            orderInfo.setOrderDetailList(orderDetails);

            orderInfo.setProcessStatus("订单未支付");
            orderInfo.setOrderStatus("未支付");
            //String tradeNo = UUID.randomUUID().toString();
            //外部订单号
            String outTradeNo = "GAOFAN" + CommonUtil.getCurrentDateStr() + System.currentTimeMillis();
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setCreateTime(new Date());
            orderInfo.setTotalAmount(getTotalPrice(cartInfos));
            orderInfo.setUserId(userId);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR,1);
            orderInfo.setExpireTime(calendar.getTime());
            orderInfo.setPaymentWay(PaymentWay.ONLINE);
            String consignee = "测试收件人";
            orderInfo.setConsignee(consignee);
            String address = "测试收件人地址";
            orderInfo.setDeliveryAddress(address);
            orderInfo.setOrderComment("硅谷订单");
            String consigneeTel = "13924285707";
            orderInfo.setConsigneeTel(consigneeTel);

            String orderId = orderService.saveOrder(orderInfo);

            cartService.deleteCartById(cartInfos);

            return "redirect:http://payment.gmall.com:8088/index?orderId="+orderId;
        }
    }

    @LoginRequire(requiredLogin = true)
    @RequestMapping("toTrade")
    public String toTrade(HttpServletRequest request, HttpServletResponse response, ModelMap map){
        String userId = (String) request.getAttribute("userId");
        //如果用已经登录了，就取缓存中的数据
        //如果用户未登录，就合并购物车
        List<CartInfo> cartInfos = cartInfos = cartService.getCartCacheByChecked(userId);
        List<OrderDetail> orderDetailLists = new ArrayList<>();
        if(cartInfos !=null && cartInfos.size() >0 ){
            for (CartInfo cartInfo : cartInfos) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(cartInfo.getCartPrice());
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetailLists.add(orderDetail);
            }
        }
        map.put("totalAmount",getTotalPrice(cartInfos));
        map.put("orderDetailList",orderDetailLists);

        List<UserAddress> userAddressList = userService.getUserAddressList(userId);
        map.put("userAddressList",userAddressList);

        String tradeCode = orderService.gentradeCode(userId);
        map.put("tradeCode",tradeCode);
        return "trade";
    }

    private BigDecimal getTotalPrice(List<CartInfo> cartList) {
        BigDecimal price = new BigDecimal("0");
        if(cartList !=null && cartList.size() >0){
            for (CartInfo cartInfo : cartList) {
                    price = price.add(cartInfo.getCartPrice());
            }
        }
        return price;
    }
}
