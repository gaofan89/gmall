package com.gaofan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gaofan.gmall.bean.SpuInfo;
import com.gaofan.gmall.bean.SpuSaleAttr;
import com.gaofan.gmall.bean.SpuSaleAttrValue;
import com.gaofan.gmall.manage.mapper.SpuInfoMapper;
import com.gaofan.gmall.manage.mapper.SpuSaleAttrMapper;
import com.gaofan.gmall.manage.mapper.SpuSaleAttrValueMapper;
import com.gaofan.gmall.service.SpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.AfterTransaction;

import java.util.List;

@Service
public class SpuServiceImpl implements SpuService {

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public List<SpuInfo> getSpuInfo(String catalog3Id) {
        SpuInfo spuInfo = new SpuInfo();
        spuInfo.setCatalog3Id(catalog3Id);
        List<SpuInfo> select = spuInfoMapper.select(spuInfo);
        return select;
    }

    @Override
    public void saveSpu(SpuInfo spuInfo) {

        spuInfoMapper.insertSelective(spuInfo);
        List<SpuSaleAttr> attrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr attr : attrList) {
            attr.setSpuId(spuInfo.getId());

            List<SpuSaleAttrValue> valueList = attr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue value : valueList) {
                value.setSpuId(spuInfo.getId());
                spuSaleAttrValueMapper.insert(value);
            }

            spuSaleAttrMapper.insert(attr);
        }


    }
}
