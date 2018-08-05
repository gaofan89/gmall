package com.gaofan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gaofan.gmall.bean.SkuInfo;
import com.gaofan.gmall.manage.mapper.SkuInfoMapper;
import com.gaofan.gmall.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SkuInfoServiceImpl implements SkuInfoService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Override
    public List<SkuInfo> getSkuListByPid(String spuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);

        return skuInfoMapper.select(skuInfo);
    }
}
