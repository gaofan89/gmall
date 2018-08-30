package com.gaofan.gmall.service;

import com.gaofan.gmall.bean.CartInfo;

import java.util.List;

public interface CartService {
    CartInfo getCartInfoBySkuId(CartInfo cartInfo);

    void addCartInfo(CartInfo cartInfo);

    void updateCartInfo(CartInfo cartInfoDb);

    void syncCache(String userId);

    List<CartInfo> getCartListCache(String userId);

    List<CartInfo> checkCart(CartInfo info);

    List<CartInfo> getCartCacheByChecked(String userId);

    void deleteCartById(List<CartInfo> cartInfos);

    void combineCartInfo(List<CartInfo> carts, String id);
}
