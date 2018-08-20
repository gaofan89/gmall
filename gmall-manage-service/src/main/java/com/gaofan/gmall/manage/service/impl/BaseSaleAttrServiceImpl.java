package com.gaofan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gaofan.gmall.bean.BaseSaleAttr;
import com.gaofan.gmall.bean.SpuSaleAttr;
import com.gaofan.gmall.bean.SpuSaleAttrValue;
import com.gaofan.gmall.manage.mapper.BaseSaleAttrMapper;
import com.gaofan.gmall.manage.mapper.SpuSaleAttrMapper;
import com.gaofan.gmall.manage.mapper.SpuSaleAttrValueMapper;
import com.gaofan.gmall.service.BaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class BaseSaleAttrServiceImpl implements BaseSaleAttrService {

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Override
    public List<BaseSaleAttr> getBaseSaleAttr() {
        return baseSaleAttrMapper.selectAll();
    }

    @Override
    public List<SpuSaleAttr> getSaleAttrListByPid(String spuId) {
        SpuSaleAttr attr = new SpuSaleAttr();
        attr.setSpuId(spuId);
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.select(attr);
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrs) {
            SpuSaleAttrValue spuSaleAttrValue = new SpuSaleAttrValue();

            spuSaleAttrValue.setSpuId(spuId);
            spuSaleAttrValue.setSaleAttrId(spuSaleAttr.getSaleAttrId());

            List<SpuSaleAttrValue> select = spuSaleAttrValueMapper.select(spuSaleAttrValue);

            spuSaleAttr.setSpuSaleAttrValueList(select);
        }

        return spuSaleAttrs;
    }
}
