package com.gaofan.gmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gaofan.gmall.bean.*;
import com.gaofan.gmall.service.AttrService;
import com.gaofan.gmall.service.EsListService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

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

        map.put("keyword",skuLsParam.getKeyword());

        Set<String> valueIds = new HashSet<>();
        for (SkuLsInfo skuLsInfo : skuLsInfos) {
            List<SkuLsAttrValue> skuAttrValueList = skuLsInfo.getSkuAttrValueList();
            for (SkuLsAttrValue skuLsAttrValue : skuAttrValueList) {
                valueIds.add(skuLsAttrValue.getValueId());
            }
        }
        String valueIdStr = StringUtils.join(valueIds, ",");
        List<Crumb> attrValueSelectedList = new ArrayList<>();
        //封装平台属性的列表
        List<BaseAttrInfo> baseAttrInfos =  attrInfoService.getAttrInfoByVIds(valueIdStr);
        String[] valueId = skuLsParam.getValueId();
        if(valueId !=null && valueId.length >0){
            String join = StringUtils.join(valueId, ",");
            Iterator<BaseAttrInfo> iterator = baseAttrInfos.iterator();
            while (iterator.hasNext()){
                BaseAttrInfo base = iterator.next();
                List<BaseAttrValue> attrValueList = base.getAttrValueList();
                for (BaseAttrValue attrValue : attrValueList) {
                    if(join.contains(attrValue.getId())){
                        Crumb crumb = new Crumb();
                        crumb.setValueName(attrValue.getValueName());
                        String urlParam = getUrlParam(skuLsParam,attrValue.getId());
                        crumb.setUrlParam(urlParam);
                        attrValueSelectedList.add(crumb);
                        iterator.remove();
                        break;
                    }
                }
            }

        }
        map.put("attrList",baseAttrInfos);
        map.put("attrValueSelectedList",attrValueSelectedList);

        String urlParam = getUrlParam(skuLsParam);
        map.put("urlParam",urlParam);

        return "list";
    }

    //可变参数如果不传的话，默认是空数据{}
    private String getUrlParam(SkuLsParam skuLsParam,String ... id) {
        String urlParam = "";

        String catalog3Id = skuLsParam.getCatalog3Id();
        String keyword = skuLsParam.getKeyword();
        String[] valueId = skuLsParam.getValueId();
        if(StringUtils.isNotBlank(catalog3Id)){
            urlParam += "catalog3Id=" +catalog3Id;
        }
        if(StringUtils.isNotBlank(keyword)){
            if("".equals(urlParam)){
                urlParam += "keyword=" + keyword;
            }else{
                urlParam += "&keyword=" + keyword;
            }
        }
        if(valueId !=null && valueId.length >0){
            for (String s : valueId) {
                if(id !=null && id.length >0 &&  id[0].equals(s)){
                    continue;
                }
                if("".equals(urlParam)){
                    urlParam += "valueId=" + s;
                }else{
                    urlParam += "&valueId=" + s;
                }
            }
        }
        return urlParam;
    }
}
