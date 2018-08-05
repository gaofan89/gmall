package com.gaofan.gmall.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gaofan.gmall.bean.BaseSaleAttr;
import com.gaofan.gmall.bean.SpuInfo;
import com.gaofan.gmall.manage.util.MyUploadUtil;
import com.gaofan.gmall.service.BaseSaleAttrService;
import com.gaofan.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class SpuInfoController {

    @Reference
    private SpuService spuService;

    @Reference
    private BaseSaleAttrService baseSaleAttrService;

    @RequestMapping("uploadFdfs")
    public String uploadFdfs(MultipartFile file){
        String imgUrl = MyUploadUtil.uploadForFDFS(file);

        return imgUrl;
    }

    @RequestMapping("getSpuInfo")
    public List<SpuInfo> getSpuInfo(String catalog3Id){
        List<SpuInfo> list = spuService.getSpuInfo(catalog3Id);

        return list;
    }
    @RequestMapping("saveSpu")
    public String saveSpu(SpuInfo spuInfo){
        spuService.saveSpu(spuInfo);

        return "success";
    }
    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> baseSaleAttrList(){
        List<BaseSaleAttr> list = baseSaleAttrService.getBaseSaleAttr();

        return list;
    }




}
