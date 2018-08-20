package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.SkuLsInfo;
import com.gaofan.gmall.bean.SkuLsParam;

import java.util.List;

public interface EsListService {

    List<SkuLsInfo> search(SkuLsParam skuLsParam);
}
