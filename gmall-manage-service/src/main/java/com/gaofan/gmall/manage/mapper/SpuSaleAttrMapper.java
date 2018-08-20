package com.gaofan.gmall.manage.mapper;

import com.gaofan.gmall.bean.SkuSaleAttrValue;
import com.gaofan.gmall.bean.SpuSaleAttr;
import com.gaofan.gmall.bean.SpuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

public interface SpuSaleAttrMapper extends Mapper<SpuSaleAttr> {


    List<SpuSaleAttr> getSpuSaleAttrCheckedList(Map<String,String> param);

    List<SkuSaleAttrValue> selectAllSkuAttrByPid(@Param("spuId") String spuId);

}
