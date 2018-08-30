package com.gaofan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gaofan.gmall.bean.*;
import com.gaofan.gmall.manage.mapper.SkuAttrValueMapper;
import com.gaofan.gmall.manage.mapper.SkuImageMapper;
import com.gaofan.gmall.manage.mapper.SkuInfoMapper;
import com.gaofan.gmall.manage.mapper.SkuSaleAttrValueMapper;
import com.gaofan.gmall.service.SkuInfoService;
import com.gaofan.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class SkuInfoServiceImpl implements SkuInfoService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<SkuInfo> getSkuListByPid(String spuId) {
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setSpuId(spuId);
        return skuInfoMapper.select(skuInfo);
    }

    @Override
    public void saveSku(SkuInfo skuInfo) {
        
        //保存skuinfo
        skuInfoMapper.insertSelective(skuInfo);
        
        //保存平台属性值
        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue skuAttrValue : skuAttrValueList) {

            skuAttrValue.setSkuId(skuInfo.getId());
            skuAttrValueMapper.insert(skuAttrValue);
        }

        //保存销售属性
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            skuSaleAttrValue.setSkuId(skuInfo.getId());
            skuSaleAttrValueMapper.insert(skuSaleAttrValue);
        }

        //保存图片信息
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(skuInfo.getId());
            skuImageMapper.insert(skuImage);
        }


    }

    @Override
    public SkuInfo getSkuInfoById(String skuId) {
        SkuInfo skuInfo = null;
        String k = "sku:"+ skuId + ":info";
        Jedis jedis = redisUtil.getJedis();
        String skJson  =  jedis.get(k);

        if("empty".equals(skJson)){ //数据库没有数据，直接返回
            return skuInfo;
        }

        if(StringUtils.isBlank(skJson)){

            //访问数据库之前，加锁
            String lockk = "sku:"+skuId+":lock";
            String lockv = jedis.set(lockk, "1", "nx", "px", 3000);
            //String OK = jedis.set("sku:" + skuId + ":lock", "1", "nx", "px", 5000);

            if("OK".equals(lockv)){ //获得锁

                skuInfo = getSkuInfoFormDb(skuId);
                if(skuInfo != null){
                    String json = JSON.toJSONString(skuInfo);
                    jedis.set(k,json);
                }else{

                    //通知其他请求稍后再访问
                    jedis.setex(k,3000,"empty");

                }

                //释放锁
                jedis.del(lockk);


            }else{  //未获得锁

                //线程先睡一会
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //自旋
                getSkuInfoById(skuId);
            }



        }else{
            skuInfo = JSON.parseObject(skJson, SkuInfo.class);
        }

        return skuInfo;
    }

    public SkuInfo getSkuInfoFormDb(String skuId){
        //获取 skunInfo的信息
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        if(skuInfo !=null){

            //获取图片信息
            SkuImage skuImage = new SkuImage();
            skuImage.setSkuId(skuId);
            List<SkuImage> skuImages = skuImageMapper.select(skuImage);

            skuInfo.setSkuImageList(skuImages);
        }

        return skuInfo;
    }

    public List<SkuInfo> getSkuInfoByCtg3Id(String catalog3Id){
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setCatalog3Id(catalog3Id);
        List<SkuInfo> skuInfos = skuInfoMapper.select(skuInfo);

        for (SkuInfo info : skuInfos) {
            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(info.getId());

            List<SkuAttrValue> skuAttrValues = skuAttrValueMapper.select(skuAttrValue);

            info.setSkuAttrValueList(skuAttrValues);
        }

        return skuInfos;
    }

    @Override
    public boolean checkPrice(CartInfo cartInfo) {

        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(cartInfo.getSkuId());
        if(skuInfo !=null && skuInfo.getPrice().compareTo(cartInfo.getSkuPrice()) ==0){
            return true;
        }

        return false;
    }


}
