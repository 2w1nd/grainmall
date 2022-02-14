package com.w1nd.grainmall.order.controller;

import com.w1nd.grainmall.order.entity.OrderEntity;
import com.w1nd.grainmall.order.entity.OrderReturnApplyEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;
    @GetMapping("/sendMq")
    public String sendMq(@RequestParam(value = "num",defaultValue = "10") Integer num){
        for (int i = 0; i < num; i++){
            if (i%2==0){
                OrderReturnApplyEntity orderReturnApplyEntity = new OrderReturnApplyEntity();
                orderReturnApplyEntity.setId(1L);
                orderReturnApplyEntity.setCreateTime(new Date());
                orderReturnApplyEntity.setReturnName("哈哈哈");
                //配置MyRabbitConfig，让发送的对象类型的消息，可以是一个json
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderReturnApplyEntity, new CorrelationData(UUID.randomUUID().toString()));
            }else {
                OrderEntity entity = new OrderEntity();
                entity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",entity, new CorrelationData(UUID.randomUUID().toString()));
            }
        }
        return "OK";
    }

}