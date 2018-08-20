package com.gaofan.gmall.manage.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.gaofan.gmall.bean.BaseAttrInfo;
import com.gaofan.gmall.bean.BaseAttrValue;
import com.gaofan.gmall.manage.mapper.AttrInfoMapper;
import com.gaofan.gmall.manage.mapper.AttrValueMapper;
import com.gaofan.gmall.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AttrInfoServiceImpl implements AttrService {

   @Autowired
   private AttrInfoMapper attrInfoMapper;

   @Autowired
   private AttrValueMapper attrValueMapper;

    @Override
    public List<BaseAttrInfo> selectAttrInfo(String catalog3Id) {
        BaseAttrInfo baseAttrInfo=  new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        return attrInfoMapper.select(baseAttrInfo);
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {

        int i = attrInfoMapper.insertSelective(baseAttrInfo);

        for (BaseAttrValue baseAttrValue : baseAttrInfo.getAttrValueList()) {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            attrValueMapper.insert(baseAttrValue);
        }


    }

    @Override
    public void deleteAttrInfo(String infoId) {
        //BaseAttrInfo baseAttrInfo = attrInfoMapper.selectByPrimaryKey(infoId);
          BaseAttrValue baseAttrValue= new BaseAttrValue();
        baseAttrValue.setAttrId(infoId);
        /*List<BaseAttrValue> values = attrValueMapper.select(baseAttrValue);
        for (BaseAttrValue value : values) {
            attrValueMapper.deleteByPrimaryKey()
        }*/

        attrValueMapper.delete(baseAttrValue);
        attrInfoMapper.deleteByPrimaryKey(infoId);

    }

    @Override
    public List<BaseAttrValue> selectAttrValue(String infoId) {
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(infoId);
        return attrValueMapper.select(baseAttrValue);
    }

    @Override
    public List<BaseAttrInfo> getAttrListByCtg3Id(String catalog3Id) {
        BaseAttrInfo baseAttrInfo=  new BaseAttrInfo();
        baseAttrInfo.setCatalog3Id(catalog3Id);
        List<BaseAttrInfo> attrInfos = attrInfoMapper.select(baseAttrInfo);

        for (BaseAttrInfo attrInfo : attrInfos) {
            BaseAttrValue value = new BaseAttrValue();
            value.setAttrId(attrInfo.getId());
            List<BaseAttrValue> values = attrValueMapper.select(value);

            attrInfo.setAttrValueList(values);
        }
        return attrInfos;
    }

    @Override
    public List<BaseAttrInfo> getAttrInfoByVIds(String valueIdStr) {
        return attrInfoMapper.selectAttrInfoByVIds(valueIdStr);
    }
}
