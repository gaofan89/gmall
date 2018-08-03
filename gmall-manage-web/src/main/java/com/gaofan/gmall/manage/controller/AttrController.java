package com.gaofan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gaofan.gmall.bean.BaseAttrInfo;
import com.gaofan.gmall.bean.BaseAttrValue;
import com.gaofan.gmall.service.AttrService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AttrController {

    @Reference
    private AttrService attrService;

    @RequestMapping("getAttrInfo")
    public List<BaseAttrInfo> getAttrInfo(String catalog3Id){
        List<BaseAttrInfo> baseAttrInfos = attrService.selectAttrInfo(catalog3Id);

        return baseAttrInfos;
    }

    @RequestMapping("getAttrValue")
    public List<BaseAttrValue> getAttrValue(String infoId){
        List<BaseAttrValue> baseAttrValues = attrService.selectAttrValue(infoId);

        return baseAttrValues;
    }

    @RequestMapping("saveAttrInfo")
    public String saveAttrInfo(BaseAttrInfo baseAttrInfo){

         attrService.saveAttrInfo(baseAttrInfo);

        return "success";
    }

    @RequestMapping("deleteAttrInfo")
    public String deleteAttrInfo(String infoId){

         attrService.deleteAttrInfo(infoId);

        return "success";
    }

}
