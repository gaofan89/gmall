package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.CartInfo;
import com.gaofan.gmall.bean.SkuInfo;

import java.util.List;

public interface SkuInfoService {
    List<SkuInfo> getSkuListByPid(String spuId);

    void saveSku(SkuInfo skuInfo);

    SkuInfo getSkuInfoById(String skuId);

    List<SkuInfo> getSkuInfoByCtg3Id(String catalog3Id);

    boolean checkPrice(CartInfo cartInfo);

    SkuInfo getSkuInfoFormDb(String skuId);
}
