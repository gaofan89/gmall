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
import java.util.OptionalInt;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallListServiceApplicationTests {

    @Autowired
    private JestClient jestClient;

    @Reference
    private SkuInfoService skuInfoService;

    public void test(int count){

        System.out.println("test :========== "+count);
    }

    @Test
    public void testAdd(){

        int count = 5;
        test(--count);

        System.out.println("testAdd :========== "+count);
    }

    @Test
    public void testBinary(){

        int[] attr = new int[10000]; // 1 —— 10000
        for (int i = 0; i < 10000; i++) {
            attr[i] = i+1;
        }

        int[] pos = {0,10000};
        int sut = getIndex(attr,300,500,pos);
        System.out.println(sut);

    }

    public int getIndex(int[] attr, int num ,int length,int[] pos){//二分法查找
        int hign = pos[0];
        int low = pos[1];

        //int length = attr.length /2 ;  // 10000 750 250

        if(num == attr[length]){
            return length;
        }else if(num > attr[length]){
            pos[1] = length;
           int len =  (hign + length ) / 2 ;


           return getIndex(attr, num ,len,pos);
        }else{
            pos[0] = length;
            int len =  (length + low ) / 2 ;
           return  getIndex(attr, num ,len,pos);
        }

    }

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

        List<SkuInfo> skuInfos = skuInfoService.getSkuInfoByCtg3Id("61");//new ArrayList<>(); //
        //SkuInfo sku = skuInfoService.getSkuInfoFormDb("94");
        //skuInfos.add(sku);
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
