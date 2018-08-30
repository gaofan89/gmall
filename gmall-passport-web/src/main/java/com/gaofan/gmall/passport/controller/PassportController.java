package com.gaofan.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gaofan.gmall.annotation.LoginRequire;
import com.gaofan.gmall.bean.CartInfo;
import com.gaofan.gmall.bean.UserInfo;
import com.gaofan.gmall.service.CartService;
import com.gaofan.gmall.service.UserService;
import com.gaofan.gmall.util.CommonUtil;
import com.gaofan.gmall.util.CookieUtil;
import com.gaofan.gmall.util.JwtUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    private UserService userService;

    @Reference
    private CartService cartService;

    @RequestMapping("index")
    public String index(String returnUrl, ModelMap map){

        map.put("originUrl",returnUrl);

        return "index";
    }

    @RequestMapping("loginOut")
    @LoginRequire
    public String loginOut(HttpServletRequest request,HttpServletResponse response){

        String userId = (String) request.getAttribute("userId");

        if(StringUtils.isNotBlank(userId)){
            CookieUtil.setCookie(request,response,"userToken","",0,true);
        }
        return "redirect:http://list.gmall.com:8084/index";
    }

    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, HttpServletResponse response,UserInfo userInfo){

        UserInfo user = userService.login(userInfo);
        if(user == null){
            return "fail";
        }else{
            Map<String,String> map = new HashMap<>();
            map.put("userId",user.getId());
            map.put("nickName",user.getNickName());
            String salt = CommonUtil.getIp(request);
            String atguigu0328 = JwtUtil.encode("atguigu0328", map, salt);

            String cartStr = CookieUtil.getCookieValue(request,"cartListName",true);
            List<CartInfo> carts = null;
            if(StringUtils.isNotBlank(cartStr)){
                carts = JSON.parseArray(cartStr,CartInfo.class);
            }
            //合并购物车
            cartService.combineCartInfo(carts,user.getId());

            CookieUtil.setCookie(request,response,"cartListName","",0,true);

            return atguigu0328;
        }
    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(String userToken,String salt){
        Map<String,String> atguigu0328 = JwtUtil.decode("atguigu0328", userToken, salt);

        if(atguigu0328 == null){
            return "fail";
        }else{
            return "success";
        }

        /*UserInfo userInfo = userService.getById(atguigu0328.get("userId"));
        if(userInfo == null){
            return "fail";
        }else{
            return "success";
        }*/

    }
}
