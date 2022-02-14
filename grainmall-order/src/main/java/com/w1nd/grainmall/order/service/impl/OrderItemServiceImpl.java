package com.w1nd.grainmall.order.service.impl;

import com.w1nd.grainmall.order.entity.OrderEntity;
import com.w1nd.grainmall.order.entity.OrderReturnApplyEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;


import com.rabbitmq.client.Channel;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.common.utils.Query;

import com.w1nd.grainmall.order.dao.OrderItemDao;
import com.w1nd.grainmall.order.entity.OrderItemEntity;
import com.w1nd.grainmall.order.service.OrderItemService;



@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * queues:声明需要监听的所有队列
     *
     * org.springframework.amqp.core.Message
     * @param message
     *
     * 参数可以写以下内容
     * 1、Message message：原生消息详细信息。头+体
     * 2、T<发送的消息类型> OrderReturnApplyEntity content
     * 3、Channel channel 当前传输数据的通道
     *
     * Queue:可以很多人都来监听。只要收到消息，队列删除消息，而且只能有一个收到此消息
     * 场景：
     *       1）、订单服务启动多个；同一个消息，只能有一个客户端收到
     *       2)、只有一个消息完全处理完，方法运行结束，我们就可以接收到下一个消息
     *
     */
//     @RabbitHandler
//     public void receiverMessage(Message message,OrderReturnApplyEntity content,
//                                 Channel channel) throws InterruptedException {
//         //消息体
//         byte[] body = message.getBody();
//         //消息头属性信息
//         MessageProperties properties = message.getMessageProperties();
//         System.out.println("接收到消息...内容:" + content);
// //        Thread.sleep(3000);
//         System.out.println("消息处理完成=》"+content.getReturnName());
//         //channel内按顺序自增的
//         long deliveryTag = message.getMessageProperties().getDeliveryTag();
//         System.out.println("deliveryTag:"+deliveryTag);
//         //签收货物，非批量模式
//         try{
//             if (deliveryTag % 2 == 0){
//                 //收货
//                 channel.basicAck(deliveryTag,false);
//                 System.out.println("签收了货物。。。"+deliveryTag);
//             }else {
//                 //退货requeue=false 丢弃  requeue=true发挥服务器，服务器重新入队。
//                 channel.basicNack(deliveryTag,false,true);
//                 System.out.println("没有签收货物..."+deliveryTag);
//             }
//
//         }catch (Exception e){
//             //网络中断
//         }
//     }
}