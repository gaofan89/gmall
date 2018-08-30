package com.gaofan.gmall.manage;

import com.alibaba.fastjson.JSON;
import com.gaofan.gmall.bean.BaseCatalog1;
import com.gaofan.gmall.bean.BaseCatalog2;
import com.gaofan.gmall.bean.BaseCatalog3;
import com.gaofan.gmall.manage.mapper.BaseCatalog1Mapper;
import com.gaofan.gmall.manage.mapper.BaseCatalog2Mapper;
import com.gaofan.gmall.manage.mapper.BaseCatalog3Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;
    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;
    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Test
    public void contextLoads() throws IOException {
        Map<String,Object> json = new HashMap<String,Object>();
        List<BaseCatalog1> baseCatalog1s = baseCatalog1Mapper.selectAll();

        for (BaseCatalog1 baseCatalog1 : baseCatalog1s) {
            BaseCatalog2 baseCatalog2 = new BaseCatalog2();
            baseCatalog2.setCatalog1Id(baseCatalog1.getId());
            List<BaseCatalog2> list = baseCatalog2Mapper.select(baseCatalog2);
            for (BaseCatalog2 catalog2 : list) {
                BaseCatalog3 baseCatalog3 = new BaseCatalog3();
                baseCatalog3.setCatalog2Id(catalog2.getId());
                List<BaseCatalog3> list2 =baseCatalog3Mapper.select(baseCatalog3);
                catalog2.setBaseCatalog3List(list2);
            }

            json.put(baseCatalog1.getId(),list);
        }


        FileOutputStream fos = new FileOutputStream("D:/catalog.json");

        String str = JSON.toJSONString(json);
        System.out.println(str);

        fos.write(str.getBytes("UTF-8"));


    }

}
