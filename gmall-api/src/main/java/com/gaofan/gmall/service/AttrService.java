package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.BaseAttrInfo;
import com.gaofan.gmall.bean.BaseAttrValue;

import java.util.List;

public interface AttrService {
    List<BaseAttrInfo> selectAttrInfo(String catalog3Id);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);

    void deleteAttrInfo(String infoId);

    List<BaseAttrValue> selectAttrValue(String infoId);
}
