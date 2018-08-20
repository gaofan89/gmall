package com.gaofan.gmall.list;

import com.alibaba.dubbo.config.annotation.Reference;
import com.gaofan.gmall.bean.SkuAttrValue;
import com.gaofan.gmall.bean.SkuInfo;
import com.gaofan.gmall.bean.SkuLsInfo;
import com.gaofan.gmall.service.SkuInfoService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Reference
    private SkuInfoService skuInfoService;

    @Test
    public void search(){

        Search search = new Search.Builder("").addIndex("gmall").addType("SkuLsInfo")
                .build();
        List<SkuLsInfo> skuLsInfos = new ArrayList<>();
        try {
            SearchResult execute = jestClient.execute(search);
            List<SearchResult.Hit<SkuLsInfo, Void>> hits = execute.getHits(SkuLsInfo.class);
            for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {

                SkuLsInfo source = hit.source;

                skuLsInfos.add(source);

            }
            System.out.println(skuLsInfos.size());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void test(){
        List<SkuInfo> skuInfos = skuInfoService.getSkuInfoByCtg3Id("61");

        System.out.println(skuInfos.size());
    }

    @Test
    public void contextLoads() {

        List<SkuInfo> skuInfos = skuInfoService.getSkuInfoByCtg3Id("61");

        List<SkuLsInfo> skuLsInfos = new ArrayList<>();
        for (SkuInfo skuInfo : skuInfos) {



            SkuLsInfo skuLsInfo = new SkuLsInfo();

            try {
                BeanUtils.copyProperties(skuLsInfo,skuInfo);
                skuLsInfos.add(skuLsInfo);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (SkuLsInfo skuLsInfo : skuLsInfos) {

            Index build = new Index.Builder(skuLsInfo).index("gmall").type("SkuLsInfo").id(skuLsInfo.getId()).build();

            try {
                jestClient.execute(build);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}
