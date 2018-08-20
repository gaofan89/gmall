package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.SpuImage;
import com.gaofan.gmall.bean.SpuInfo;
import com.gaofan.gmall.bean.SpuSaleAttr;

import java.util.List;
import java.util.Map;

public interface SpuService {

    List<SpuInfo> getSpuInfo(String catalog3Id);

    void saveSpu(SpuInfo spuInfo);

    List<SpuImage> getImageListByPid(String spuId);

    List<SpuSaleAttr> getSpuSaleAttrCheckedList(Map<String,String> param);

    String getAllSkuAttrByPid(String spuId);
}
