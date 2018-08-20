package com.gaofan.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gaofan.gmall.bean.SkuInfo;
import com.gaofan.gmall.bean.SpuInfo;
import com.gaofan.gmall.bean.SpuSaleAttr;
import com.gaofan.gmall.service.SkuInfoService;
import com.gaofan.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    private SkuInfoService skuInfoService;

    @Reference
    private SpuService spuService;

    @RequestMapping("{skuId}.html")
    public String index(@PathVariable("skuId") String skuId,ModelMap map){

        SkuInfo skuInfo = skuInfoService.getSkuInfoById(skuId);

        map.put("skuInfo",skuInfo);

        Map<String,String> param = new HashMap<String,String>();
        param.put("spuId",skuInfo.getSpuId());
        param.put("skuId",skuId);
        List<SpuSaleAttr> spuSaleAttrs =  spuService.getSpuSaleAttrCheckedList(param);

        map.put("spuSaleAttrListCheckBySku",spuSaleAttrs);

        String skuAttrJson = spuService.getAllSkuAttrByPid(skuInfo.getSpuId());
        map.put("skuAttrJson",skuAttrJson);

        return "item";
    }

    @RequestMapping("demo")
    public String index(ModelMap map){
        map.put("hello","helloWorld");

        List<SpuInfo> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SpuInfo spuInfo = new SpuInfo();
            spuInfo.setSpuName("小米"+i);
            spuInfo.setId(String.valueOf(i+100));
            list.add(spuInfo);
        }
        map.put("spuInfo",list);


        return "demo";
    }
}
