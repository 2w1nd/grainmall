package com.w1nd.grainmall.order.listener;

import com.rabbitmq.client.Channel;
import com.w1nd.grainmall.order.entity.OrderEntity;
import com.w1nd.grainmall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RabbitListener(queues = "order.release.order.queue")
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
        log.info("*********订单关闭************");
        try {
            orderService.closeOrder(entity);

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            // 修改失败 拒绝消息 使消息重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
