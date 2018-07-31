package com.gaofan.gmall.manage.controller;

import com.gaofan.gmall.bean.UserInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class IndexController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("index")
    public  String index(){

        return "index";
    }


    @RequestMapping("getInfo")
    @ResponseBody
    public List<UserInfo> userInfoList(){
        Gson gson = new Gson();
        String json =  restTemplate.getForObject("http://192.168.0.52:8080/userInfoList",String.class);
        //List<Person> list2 = gson.fromJson(personListJsonString, new TypeToken<ArrayList<Person>>() {}.getType());
        List<UserInfo> list2 = gson.fromJson(json, new TypeToken<ArrayList<UserInfo>>() {}.getType());
        //System.out.println(json);
        return list2;
    }


    @RequestMapping("addAttr")
    public String addAttr(){
        return "addAttr";
    }
}
