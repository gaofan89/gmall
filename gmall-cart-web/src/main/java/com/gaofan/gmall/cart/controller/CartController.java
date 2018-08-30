package com.gaofan.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.gaofan.gmall.annotation.LoginRequire;
import com.gaofan.gmall.bean.CartInfo;
import com.gaofan.gmall.bean.SkuInfo;
import com.gaofan.gmall.service.CartService;
import com.gaofan.gmall.service.SkuInfoService;
import com.gaofan.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class CartController {

    @Reference
    private SkuInfoService skuInfoService;

    @Reference
    private CartService cartService;


    @RequestMapping("checkCart")
    @LoginRequire(requiredLogin = false)
    public String checkCart(HttpServletRequest request,HttpServletResponse response,
                            CartInfo info,ModelMap map){
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartList =  null;
        if(StringUtils.isBlank(userId)){
            String cookieValue = CookieUtil.getCookieValue(request, "cartListName", true);
            cartList = JSON.parseArray(cookieValue, CartInfo.class);
            for (CartInfo cartInfo : cartList) {
                if(cartInfo.getSkuId().equals(info.getSkuId())){
                    cartInfo.setIsChecked(info.getIsChecked());
                }
            }
            CookieUtil.setCookie(request,response,"cartListName",JSON.toJSONString(cartList),60*60*24*7,true);
        }else{
            info.setUserId(userId);
            //CartInfo cartInfoDb = cartService.getCartInfoBySkuId(info);

            cartList = cartService.checkCart(info);

//            cartInfoDb.setIsChecked(info.getIsChecked());
//            cartService.updateCartInfo(cartInfoDb);
//            cartService.syncCache(userId);
//
//            cartList = cartService.getCartListCache(userId);

        }
        map.put("cartList",cartList);
        BigDecimal totalPrice = getTotalPrice(cartList);
        map.put("totalPrice",totalPrice);

        return "cartListInner";
    }

    @RequestMapping("cartList")
    @LoginRequire(requiredLogin = false)
    public String cartList(HttpServletRequest request , ModelMap map){
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartList =  null;
        if(StringUtils.isBlank(userId)){
            String cookieValue = CookieUtil.getCookieValue(request, "cartListName", true);
            cartList = JSON.parseArray(cookieValue, CartInfo.class);
        }else{
            cartList = cartService.getCartListCache(userId);
        }
        map.put("cartList",cartList);
        BigDecimal totalPrice = getTotalPrice(cartList);
        map.put("totalPrice",totalPrice);
        return "cartList";
    }

    private BigDecimal getTotalPrice(List<CartInfo> cartList) {
        BigDecimal price = new BigDecimal("0");
        if(cartList !=null && cartList.size() >0){

            for (CartInfo cartInfo : cartList) {
                if(cartInfo.getIsChecked().equals("1")){

                    price = price.add(cartInfo.getCartPrice());
                }
            }
        }

        return price;
    }

    @RequestMapping("successCart")
    public String successCart(String skuId,String skuNum,ModelMap map){
        SkuInfo skuInfo = skuInfoService.getSkuInfoById(skuId);
        map.put("skuInfo",skuInfo);
        map.put("skuNum",skuNum);
        return "success";
    }

    @RequestMapping("addToCart")
    @LoginRequire(requiredLogin = false)
    public String addCart(HttpServletRequest request, HttpServletResponse response,CartInfo cartInfo){
        String userId = (String) request.getAttribute("userId");
        String cookieName = "cartListName";
        List<CartInfo> cookieList = new ArrayList<>();
        SkuInfo infoById = skuInfoService.getSkuInfoById(cartInfo.getSkuId());

        cartInfo.setImgUrl(infoById.getSkuDefaultImg());
        cartInfo.setIsChecked("1");
        cartInfo.setSkuName(infoById.getSkuName());
        cartInfo.setSkuPrice(infoById.getPrice());
        cartInfo.setCartPrice(infoById.getPrice().multiply(new BigDecimal(cartInfo.getSkuNum())));


        if(StringUtils.isBlank(userId)){   //操作cookie
            String cookieValue = CookieUtil.getCookieValue(request, cookieName, true);

            if(StringUtils.isNotBlank(cookieValue)){
                cookieList = JSON.parseArray(cookieValue, CartInfo.class);
                boolean isExists = false;
                for (CartInfo info : cookieList) {
                    if(info.getSkuId().equals(cartInfo.getSkuId())){ //存在就覆盖
                        isExists = true;
                        info.setSkuNum(info.getSkuNum() + cartInfo.getSkuNum());
                        info.setCartPrice(info.getSkuPrice().multiply(new BigDecimal(info.getSkuNum())));
                    }
                }
                if(!isExists){ //不存在就直接添加
                    cookieList.add(cartInfo);
                }

            }else{
                cookieList.add(cartInfo);
            }

            CookieUtil.setCookie(request,response,cookieName, JSON.toJSONString(cookieList),60*60*24*7,true);

        }else{//操作db

            //判断数据库是否存在当前cartInfo的数据
            cartInfo.setUserId(userId);
            CartInfo cartInfoDb = cartService.getCartInfoBySkuId(cartInfo);
            if(cartInfoDb == null){
                cartService.addCartInfo(cartInfo);
            }else{
                cartInfoDb.setSkuNum(cartInfo.getSkuNum() + cartInfoDb.getSkuNum());
                cartInfoDb.setCartPrice(cartInfoDb.getSkuPrice().multiply(new BigDecimal(cartInfoDb.getSkuNum())));
                cartService.updateCartInfo(cartInfoDb);
            }
            //同步缓存
            cartService.syncCache(userId);
        }

        return "redirect:/successCart?skuId=" +cartInfo.getSkuId() + "&skuNum="+cartInfo.getSkuNum();
    }
}
