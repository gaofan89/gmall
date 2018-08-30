package com.gaofan.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gaofan.gmall.bean.CacheCollection;
import com.gaofan.gmall.bean.CartInfo;
import com.gaofan.gmall.cart.mapper.CartInfoMapper;
import com.gaofan.gmall.service.CartService;
import com.gaofan.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public CartInfo getCartInfoBySkuId(CartInfo cartInfo) {
        CartInfo cartInfo1 = new CartInfo();
        cartInfo1.setUserId(cartInfo.getUserId());
        cartInfo1.setSkuId(cartInfo.getSkuId());
        CartInfo info = cartInfoMapper.selectOne(cartInfo1);

        return info;
    }

    @Override
    public void addCartInfo(CartInfo cartInfo) {

        cartInfoMapper.insertSelective(cartInfo);

    }

    @Override
    public void updateCartInfo(CartInfo cartInfoDb) {
        cartInfoMapper.updateByPrimaryKeySelective(cartInfoDb);
    }

    @Override
    public void syncCache(String userId) {
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUserId(userId);
        List<CartInfo> select = cartInfoMapper.select(cartInfo);
        //cart:userId:info skuId cartInfo skuId2 cartInfo2

        Jedis jedis = redisUtil.getJedis();
        if(select == null || select.size() ==0){
            jedis.del("carts:"+userId+":info");
        }else {

            Map<String, String> redisMap = new HashMap<>();
            for (CartInfo info : select) {
                redisMap.put(info.getSkuId(), JSON.toJSONString(info));
            }
            jedis.hmset("carts:"+userId+":info",redisMap);
        }

        jedis.close();

    }

    @Override
    public List<CartInfo> getCartListCache(String userId) {

        Jedis jedis = redisUtil.getJedis();
        List<CartInfo> list = new ArrayList<>();
        List<String> strings = jedis.hvals("carts:" + userId + ":info");
        CartInfo cartInfo = null;
        for (String string : strings) {
            cartInfo = JSON.parseObject(string, CartInfo.class);
            list.add(cartInfo);
        }

        jedis.close();

        return list;
    }

    @Override
    public List<CartInfo> checkCart(CartInfo info) {
        CartInfo cartInfoDb = getCartInfoBySkuId(info);
        cartInfoDb.setIsChecked(info.getIsChecked());
        updateCartInfo(cartInfoDb);
        syncCache(info.getUserId());

        return  getCartListCache(info.getUserId());
    }

    @Override
    public List<CartInfo> getCartCacheByChecked(String userId) {
        List<CartInfo> list = getCartListCache(userId);
        List<CartInfo> lists = new ArrayList<>();
        if(list !=null & list.size() >0){
            for (CartInfo cartInfo : list) {
                if(cartInfo.getIsChecked().equals("1")){
                    lists.add(cartInfo);
                }
            }
        }
        return lists;
    }

    @Override
    public void deleteCartById(List<CartInfo> cartInfos) {
        //通过sql语句删除 delete from cart_info where id in();
        String userId = null;
        for (CartInfo cartInfo : cartInfos) {
            if(userId == null) userId = cartInfo.getUserId();
            cartInfoMapper.deleteByPrimaryKey(cartInfo.getId());
        }

        syncCache(userId);
    }

    @Override
    public void combineCartInfo(List<CartInfo> carts, String id) {
          if(carts !=null && carts.size() >0){
              for (CartInfo cart : carts) {
                  CartInfo info = getCartInfoBySkuId(cart);
                  if(info ==  null){
                      cart.setUserId(id);
                      addCartInfo(cart);
                  }else{
                      info.setSkuNum(info.getSkuNum() + cart.getSkuNum());
                      info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                      updateCartInfo(info);
                  }
              }
              syncCache(id);
          }


//        List<CartInfo> cacheList = getCartListCache(id);
//        cacheList.addAll(carts);
//        Jedis jedis = redisUtil.getJedis();
//        if(cacheList !=null && cacheList.size() >0){
//            Map<String, String> redisMap = new HashMap<>();
//            for (CartInfo info : cacheList) {
//                redisMap.put(info.getSkuId(), JSON.toJSONString(info));
//            }
//            jedis.hmset("carts:"+id+":info",redisMap);
//        }

    }

    public void updateForRedis(String id){
        CacheCollection.UnSuccessList.add(id);
        CacheCollection.updateList.add(id);

        //调用update方法更新数据

        //从UnSuccessList中删除更新成功的id
        CacheCollection.updateList.add(id);

    }
}
