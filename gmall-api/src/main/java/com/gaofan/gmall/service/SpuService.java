package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.SpuInfo;

import java.util.List;

public interface SpuService {
    List<SpuInfo> getSpuInfo(String catalog3Id);

    void saveSpu(SpuInfo spuInfo);
}
