package com.gaofan.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gaofan.gmall.bean.UserAddress;
import com.gaofan.gmall.bean.UserInfo;
import com.gaofan.gmall.service.UserService;
import com.gaofan.gmall.user.mapper.UserAddressMapper;
import com.gaofan.gmall.user.mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserInfoMapper userInfoMapper;
    @Autowired
    UserAddressMapper userAddressMapper;

    @Override
    public List<UserInfo> userInfoList() {
        return userInfoMapper.selectAll();
    }

    @Override
    public UserInfo getById(String id) {
        return userInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {

        UserInfo userInfo1 = userInfoMapper.selectOne(userInfo);
        return userInfo1;
    }

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        Example example = new Example(UserAddress.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        return userAddressMapper.selectByExample(example);
    }
}
