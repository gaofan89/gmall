package com.gaofan.gmall.order.task;

import com.gaofan.gmall.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class OrderCheckTask {

    @Autowired
    private OrderService orderService; //clearOrderInfoByTime

    @Scheduled(cron = "0/55 * *  * * ?")//秒 分 时 天 月 周 年
    public void clearOrderInfoByTime() throws InterruptedException {

        System.out.println("定时检查过期订单，删除过期订单，由orderService来执行");
    }
}
