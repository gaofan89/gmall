package com.gaofan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gaofan.gmall.bean.SpuImage;
import com.gaofan.gmall.bean.SpuInfo;
import com.gaofan.gmall.bean.SpuSaleAttr;
import com.gaofan.gmall.bean.SpuSaleAttrValue;
import com.gaofan.gmall.manage.mapper.SpuImageMapper;
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
    private SpuImageMapper spuImageMapper;

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

        //保存spu
        spuInfoMapper.insertSelective(spuInfo);

        List<SpuSaleAttr> attrList = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr attr : attrList) {
            attr.setSpuId(spuInfo.getId());

            //保存商品销售属性值
            List<SpuSaleAttrValue> valueList = attr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue value : valueList) {
                value.setSpuId(spuInfo.getId());
                spuSaleAttrValueMapper.insert(value);
            }

            //保存商品销售属性
            spuSaleAttrMapper.insert(attr);
        }

        //保存商品图片
        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        for (SpuImage spuImage : spuImageList) {
            spuImage.setSpuId(spuInfo.getId());
            spuImageMapper.insert(spuImage);
        }


    }
}
