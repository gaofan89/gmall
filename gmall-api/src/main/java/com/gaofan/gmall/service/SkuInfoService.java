package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.SkuInfo;

import java.util.List;

public interface SkuInfoService {
    List<SkuInfo> getSkuListByPid(String spuId);
}
