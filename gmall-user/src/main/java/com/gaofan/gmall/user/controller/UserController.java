package com.gaofan.gmall.user.controller;

import com.gaofan.gmall.bean.UserInfo;
import com.gaofan.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("userInfo")
    public ResponseEntity<List<UserInfo>> userInfoList(){
        List<UserInfo> list = userService.userInfoList();

        return ResponseEntity.ok(list); //ResponseEntity.ok(list);
    }

    @RequestMapping("hello")
    public String getHello(){

        return "hello World";
    }
}
