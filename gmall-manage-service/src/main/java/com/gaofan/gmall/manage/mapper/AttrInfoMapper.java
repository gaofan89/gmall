package com.gaofan.gmall.manage.mapper;

import com.gaofan.gmall.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface AttrInfoMapper extends Mapper<BaseAttrInfo> {

    List<BaseAttrInfo> selectAttrInfoByVIds(@Param("valueIdStr") String valueIdStr);
}
