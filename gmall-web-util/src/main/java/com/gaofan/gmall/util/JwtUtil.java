package com.gaofan.gmall.util;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @param
 * @return
 */
public class JwtUtil {

    public static void main(String[] args){
        Map<String, String> map = new HashMap<>();
        map.put("userId","11414144");
        map.put("nickName","高凡");
        String atguigu0328 = encode("atguigu0328", map, "127.0.0.1");
        System.out.println(atguigu0328);

        Map atguigu03281 = decode("atguigu0328", atguigu0328, "127.1.0.9");
        System.out.println(atguigu03281);

        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] code = base64UrlCodec.decode("eyJuaWNrTmFtZSI6IumrmOWHoSIsInVzZXJJZCI6IjExNDE0MTQ0In0");
        System.out.println(new String(code));
    }


    /***
     * jwt加密
     * @param key
     * @param map
     * @param salt
     * @return
     */
    public static String encode(String key,Map map,String salt){

        if(salt!=null){
            key+=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);
        jwtBuilder.addClaims(map);

        String token = jwtBuilder.compact();
        return token;
    }

    /***
     * jwt解密
     * @param key
     * @param token
     * @param salt
     * @return
     * @throws SignatureException
     */
    public static  Map decode(String key,String token,String salt) { //throws SignatureException{
        if(salt!=null){
            key+=salt;
        }
        Claims map = null;
        try {
            map = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();

            System.out.println("map.toString() = " + map.toString());
        }catch (Exception e){
            System.out.println("本次token["+token+"]解密失败");
        }
        return map;

    }

}
