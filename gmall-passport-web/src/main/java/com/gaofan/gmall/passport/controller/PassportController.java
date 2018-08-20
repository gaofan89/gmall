package com.gaofan.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gaofan.gmall.bean.UserInfo;
import com.gaofan.gmall.service.UserService;
import com.gaofan.gmall.util.CommonUtil;
import com.gaofan.gmall.util.JwtUtil;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {

    @Reference
    private UserService userService;

    @RequestMapping("index")
    public String index(String returnUrl, ModelMap map){

        map.put("originUrl",returnUrl);

        return "index";
    }
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request,UserInfo userInfo){

        UserInfo user = userService.login(userInfo);
        if(user == null){
            return "fail";
        }else{
            Map<String,String> map = new HashMap<>();
            map.put("userId",user.getId());
            map.put("nickName",user.getNickName());
            String salt = CommonUtil.getIp(request);
            String atguigu0328 = JwtUtil.encode("atguigu0328", map, salt);
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
