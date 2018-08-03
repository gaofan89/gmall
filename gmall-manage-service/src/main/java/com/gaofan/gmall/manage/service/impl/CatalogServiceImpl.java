package com.gaofan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gaofan.gmall.bean.BaseAttrInfo;
import com.gaofan.gmall.bean.BaseCatalog1;
import com.gaofan.gmall.bean.BaseCatalog2;
import com.gaofan.gmall.bean.BaseCatalog3;
import com.gaofan.gmall.manage.mapper.BaseCatalog1Mapper;
import com.gaofan.gmall.manage.mapper.BaseCatalog2Mapper;
import com.gaofan.gmall.manage.mapper.BaseCatalog3Mapper;
import com.gaofan.gmall.service.CatalogService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CatalogServiceImpl implements CatalogService {


    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;


    @Override
    public List<BaseCatalog1> getCatalog1() {
        List<BaseCatalog1> list = baseCatalog1Mapper.selectAll();
        return list;
    }

    public List<BaseCatalog2> getCatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2= new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        List<BaseCatalog2> list = baseCatalog2Mapper.select(baseCatalog2);
        return list;
    }

    public List<BaseCatalog3> getCatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3= new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        List<BaseCatalog3> list = baseCatalog3Mapper.select(baseCatalog3);
        return list;
    }

}
