package com.gaofan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gaofan.gmall.bean.BaseAttrInfo;
import com.gaofan.gmall.bean.BaseCatalog1;
import com.gaofan.gmall.bean.BaseCatalog2;
import com.gaofan.gmall.bean.BaseCatalog3;
import com.gaofan.gmall.service.CatalogService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CatalogController {

    @Reference
    private CatalogService catalogService;

    @RequestMapping("getCatalog1")
    public List<BaseCatalog1> getCatalog1(){

        List<BaseCatalog1> list = catalogService.getCatalog1();
        return list;
    }
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2> getCatalog2(String catalog1Id){

        List<BaseCatalog2> list = catalogService.getCatalog2(catalog1Id);
        return list;
    }
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3> getCatalog3(String catalog2Id){

        List<BaseCatalog3> list = catalogService.getCatalog3(catalog2Id);
        return list;
    }
}
