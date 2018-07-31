package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.UserInfo;

import java.util.List;

public interface UserService {
    List<UserInfo> userInfoList();

    UserInfo getById(String id);
}
