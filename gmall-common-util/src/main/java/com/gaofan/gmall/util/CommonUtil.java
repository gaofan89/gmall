package com.gaofan.gmall.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {

    public static final String USER_TOKEN_KEY = "atguigu0328";


    public static final String PAYMENT_SUCCESS_MQ = "PAYMENT_SUCCESS_MQ";
    public static final String PAYMENT_CHECK_MQ = "PAYMENT_CHECK_MQ";

    public static final String DELIVERY_MQ = "ORDER_RESULT_QUEUE";


    public static String getIp(HttpServletRequest request){

        String ip = request.getHeader("x-forwarded-for");
        if(StringUtils.isBlank(ip)){

            ip = request.getRemoteAddr();
        }
        if(StringUtils.isBlank(ip)){

            ip = "127.0.0.1";
        }
        return ip;
    }

    public static String getTradeCode(String userId){

        return "userId:"+userId+":tradeCode";
    }

    public static String getCurrentDateStr(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
}
