package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.BaseSaleAttr;
import com.gaofan.gmall.bean.SpuSaleAttr;

import java.util.List;

public interface BaseSaleAttrService {
    List<BaseSaleAttr> getBaseSaleAttr();

    List<SpuSaleAttr> getSaleAttrListByPid(String spuId);
}
