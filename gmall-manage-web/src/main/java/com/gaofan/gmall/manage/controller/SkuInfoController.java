package com.gaofan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gaofan.gmall.bean.SkuInfo;
import com.gaofan.gmall.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SkuInfoController {

    @Reference
    private SkuInfoService skuInfoService;

    @RequestMapping("/saveSku")
    public String saveSku(SkuInfo skuInfo){
        skuInfoService.saveSku(skuInfo);
        return "success";
    }

    @RequestMapping("/getSkuListByPid")
    public List<SkuInfo> getSkuListByPid(String spuId){

        return skuInfoService.getSkuListByPid(spuId);
    }
}
