package com.gaofan.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.gaofan.gmall.bean.OrderDetail;
import com.gaofan.gmall.bean.OrderInfo;
import com.gaofan.gmall.order.mapper.OrderDetailMapper;
import com.gaofan.gmall.order.mapper.OrderInfoMapper;
import com.gaofan.gmall.service.OrderService;
import com.gaofan.gmall.util.ActiveMQUtil;
import com.gaofan.gmall.util.CommonUtil;
import com.gaofan.gmall.util.RedisUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.print.attribute.standard.OrientationRequested;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void updatePaySuccess(OrderInfo orderInfo) {
        Example example = new Example(OrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("outTradeNo",orderInfo.getOutTradeNo());
        orderInfoMapper.updateByExampleSelective(orderInfo,example);

    }

    @Override
    public void sendDeliveryMq(String outTradeNo) {
        ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
        try {
            OrderInfo orderInfo = getByOutTradeNo(outTradeNo);

            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderInfo.getId());
            List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);
            orderInfo.setOrderDetailList(orderDetails);

            textMessage.setText(JSON.toJSONString(orderInfo));
            activeMQUtil.sendMq(CommonUtil.DELIVERY_MQ, textMessage);
            System.out.println("发送订单的消息，给库存");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String gentradeCode(String userId) {
        String key = CommonUtil.getTradeCode(userId);
        String tradeCode = UUID.randomUUID().toString();

        Jedis jedis = redisUtil.getJedis();

        jedis.setex(key,60*30,tradeCode);

        jedis.close();

        return tradeCode;

    }

    @Override
    public boolean checkTradeCode(String tradeCode,String userId) {
        String key = CommonUtil.getTradeCode(userId);

        Jedis jedis = redisUtil.getJedis();
        String code = jedis.get(key);
        if(StringUtils.isNotBlank(tradeCode) && tradeCode.equals(code)){
            jedis.del(key);
            return true;
        }

        return false;
    }

    @Override
    public String saveOrder(OrderInfo orderInfo) {

        orderInfoMapper.insertSelective(orderInfo);

        List<OrderDetail> orderDetails =  orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetails) {
            orderDetail.setOrderId(orderInfo.getId());

            orderDetailMapper.insert(orderDetail);
        }

        return orderInfo.getId();
    }

    @Override
    public OrderInfo getById(String orderId) {

        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderId);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderInfo.getId());
        List<OrderDetail> orderDetails = orderDetailMapper.select(orderDetail);

        orderInfo.setOrderDetailList(orderDetails);

        return orderInfo;
    }

    @Override
    public OrderInfo getByOutTradeNo(String out_trade_no) {
        Example example = new Example(OrderInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("outTradeNo",out_trade_no);
        return orderInfoMapper.selectOneByExample(example);
    }
}
