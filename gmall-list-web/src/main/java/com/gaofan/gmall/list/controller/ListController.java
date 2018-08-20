package com.gaofan.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gaofan.gmall.bean.BaseAttrInfo;
import com.gaofan.gmall.bean.SkuLsAttrValue;
import com.gaofan.gmall.bean.SkuLsInfo;
import com.gaofan.gmall.bean.SkuLsParam;
import com.gaofan.gmall.service.AttrService;
import com.gaofan.gmall.service.EsListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Controller
public class ListController {

    @Reference
    private EsListService esListService;

    @Reference
    private AttrService attrInfoService;

    @RequestMapping("/index")
    public String index(){

        return "index";
    }

    @RequestMapping("/list.html")
    public String list(SkuLsParam skuLsParam,ModelMap map){

        List<SkuLsInfo> skuLsInfos =  esListService.search(skuLsParam);

        map.put("skuLsInfoList",skuLsInfos);

        Set<String> valueIds = new HashSet<>();
        for (SkuLsInfo skuLsInfo : skuLsInfos) {
            List<SkuLsAttrValue> skuAttrValueList = skuLsInfo.getSkuAttrValueList();
            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                valueIds.add(skuLsAttrValue.getValueId());
            }
        }
        String valueIdStr = StringUtils.join(valueIds, ",");
        //封装平台属性的列表
        List<BaseAttrInfo> baseAttrInfos =  attrInfoService.getAttrInfoByVIds(valueIdStr);

        map.put("attrList",baseAttrInfos);

        return "list";
    }
}
