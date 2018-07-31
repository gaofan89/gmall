package com.gaofan.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gaofan.gmall.bean.UserInfo;
import com.gaofan.gmall.service.UserService;
import com.gaofan.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;

    @Override
    public List<UserInfo> userInfoList() {
        return userInfoMapper.selectAll();
    }

    @Override
    public UserInfo getById(String id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }
}
