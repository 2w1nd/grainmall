package com.w1nd.grainmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.w1nd.common.exception.NoStockException;
import com.w1nd.common.to.mq.OrderTo;
import com.w1nd.common.utils.R;
import com.w1nd.common.vo.MemberResponseVO;
import com.w1nd.grainmall.order.constant.OrderConstant;
import com.w1nd.grainmall.order.constant.PayConstant;
import com.w1nd.grainmall.order.entity.OrderItemEntity;
import com.w1nd.grainmall.order.entity.PaymentInfoEntity;
import com.w1nd.grainmall.order.feign.CartFeignService;
import com.w1nd.grainmall.order.feign.MemberFeignService;
import com.w1nd.grainmall.order.feign.ProductFeignService;
import com.w1nd.grainmall.order.feign.WmsFeignService;
import com.w1nd.grainmall.order.interceptor.LoginUserInterceptor;
import com.w1nd.grainmall.order.service.OrderItemService;
import com.w1nd.grainmall.order.service.PaymentInfoService;
import com.w1nd.grainmall.order.to.OrderCreateTo;
import com.w1nd.grainmall.order.vo.*;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.common.utils.Query;
import com.w1nd.grainmall.order.enume.OrderStatusEnum;

import com.w1nd.grainmall.order.dao.OrderDao;
import com.w1nd.grainmall.order.entity.OrderEntity;
import com.w1nd.grainmall.order.service.OrderService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;



@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    WmsFeignService wmsFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderItemService orderItemService;

    @Autowired
    PaymentInfoService paymentInfoService;

    private ThreadLocal<OrderSubmitVo> confirmVoThreadLocal = new ThreadLocal<>();

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 展示订单的所有数据
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        MemberResponseVO memberResponseVO = LoginUserInterceptor.loginUser.get();
        // System.out.println("主线程..."+Thread.currentThread().getId());
        //获取之前的请求
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //异步任务编排
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(() -> {
            //1、远程查询所有的收货地址列表
            // System.out.println("member线程..."+Thread.currentThread().getId());
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberAddressVo> address = memberFeignService.getAddress(memberResponseVO.getId());
            confirmVo.setAddress(address);
        }, executor);

        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //2、远程查询购物车所有选中的购物项
            // System.out.println("cart线程..."+Thread.currentThread().getId());
            //每一个线程都来共享之前的请求数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> items = cartFeignService.getCurrentUserCartItems();
            confirmVo.setItems(items);
            //feign在远程调用之前要构造请求，调用很多拦截器RequestInterceptor interceptor: requestInterceptors
        }, executor).thenRunAsync(()->{
            //查询库存信息
            List<OrderItemVo> items = confirmVo.getItems();
            List<Long> collect = items.stream().map(item -> item.getSkuId()).collect(Collectors.toList());

            R hasStock = wmsFeignService.getSkusHasStock(collect);
            List<SkuStockVo> data = hasStock.getData(new TypeReference<List<SkuStockVo>>() {
            });
            if (data != null){
                Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
                confirmVo.setStocks(map);
            }

        },executor);


        //3、查询用户积分
        Integer integration = memberResponseVO.getIntegration();
        confirmVo.setIntegration(integration);

        //4、其他数据自动计算

        //5、TODO 防重令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX+memberResponseVO.getId(),token,30, TimeUnit.MINUTES);
        confirmVo.setOrderToken(token);
        CompletableFuture.allOf(getAddressFuture,cartFuture).get();
        return confirmVo;
    }

    /**
     *      * 本地事务，在分布式系统，只能控制住自己的回滚，控制不了其他服务的回滚
     *      * 分布式事务：最大原因，网络原因+分布式机器
     *      *
     * @param submitVo
     * @return
     *
     *
     * 本地事务失效问题
     *  同一个对象内事务方法互调默认失效，原因是绕过了代理对象，事务使用代理对象来控制的
     *  解决：使用代理对象来调用事务方法
     *  1. 引入spring-boot-starter-aop，引入aspenctj
     *  2.
     */
    @GlobalTransactional
    @Transactional
    @Override
    public SubmitOrderResponseVo submitOrder(OrderSubmitVo submitVo) {
        confirmVoThreadLocal.set(submitVo);
        SubmitOrderResponseVo response = new SubmitOrderResponseVo();
        MemberResponseVO memberResponseVO = LoginUserInterceptor.loginUser.get();
        response.setCode(0);
        // 1、验证令牌【令牌的对比和删除必须保证原子性】
        // 0令牌失败 -1删除成功
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        String orderToken = submitVo.getOrderToken();
        // 原子验证令牌和删除令牌
        Long result = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVO.getId()), orderToken);
        if (result == 0L) {
            // 令牌验证失败
            response.setCode(1);
            return response;
        } else {
            // 令牌验证成功 下单 去创建订单 验证令牌 核算价格 锁定库存
            // 1、创建订单，订单项等信息
            OrderCreateTo order = createOrder();
            System.out.println("订单为：" + order);
            // 2、验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal payPrice = submitVo.getPayPrice();
            if (Math.abs(payAmount.subtract(payPrice).doubleValue()) <0.01){
                // 金额对比成功
                // 3、保持订单
                saveOrder(order);
                // 4、库存锁定,只要有异常回滚订单数据。订单号，订单项信息（skuId,skuName,num）
                WareSkuLockVo wareSkuLockVo = new WareSkuLockVo();
                wareSkuLockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> orderItemVos = order.getOrderItems().stream().map(item -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    orderItemVo.setSkuId(item.getSkuId());
                    orderItemVo.setCount(item.getSkuQuantity());
                    orderItemVo.setTitle(item.getSkuName());
                    return orderItemVo;
                }).collect(Collectors.toList());
                wareSkuLockVo.setLocks(orderItemVos);
                // TODO 远程锁库存
                // 库存成功了，但是由于网络原因超时了，订单回滚，库存不回滚

                // 为了保证高并发，
                R r = wmsFeignService.orderLockStock(wareSkuLockVo);
                if (r.getCode() == 0){
                    //锁成功了
                    rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
                    // 如果这里有其他业务发生异常，也会出现远程服务假失败
                    response.setOrder(order.getOrder());
                    return response;
                }else {
                    System.out.println(r.get("msg"));
                    //锁定失败
                    throw new NoStockException((String) r.get("msg"));
                }
            }else {
                response.setCode(2);
                return response;
            }
        }
//        String redisToken = redisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVO.getId());
//        if (orderToken != null && orderToken.equals(redisToken)){
//            //令牌验证通过
//            redisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberResponseVO.getId());
//        }else {
//            //不通过
//        }
    }

    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {
        OrderEntity order_sn = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order_sn;
    }

    /**
     * 关闭订单
     * @param entity
     */
    @Override
    public void closeOrder(OrderEntity entity) {
        //查询当前这个订单地最新状态
        OrderEntity orderEntity = this.getById(entity.getId());
        if (orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()){
            // 取消订单
            OrderEntity update = new OrderEntity();
            update.setId(entity.getId());
            update.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(update);
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderEntity,orderTo);
            //发给MQ一个
            rabbitTemplate.convertAndSend("order-event-exchange","order.release.other",orderTo);
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        PayVo payVo = new PayVo();
        OrderEntity order = this.getOrderByOrderSn(orderSn);
        //支付金额设置为两位小数，否则会报错
        BigDecimal bigDecimal = order.getPayAmount().setScale(2, BigDecimal.ROUND_UP);
        payVo.setTotal_amount(bigDecimal.toString());
        payVo.setOut_trade_no(order.getOrderSn());
        List<OrderItemEntity> order_sn = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));
        OrderItemEntity entity = order_sn.get(0);
        //订单名称
        payVo.setSubject(entity.getSkuName());
        //商品描述
        payVo.setBody(entity.getSkuAttrsVals());
        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberResponseVO memberResponseVO = LoginUserInterceptor.loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>().eq("member_id",memberResponseVO.getId()).orderByDesc("id")
        );
        List<OrderEntity> order_sn = page.getRecords().stream().map(order -> {
            List<OrderItemEntity> entities = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
            order.setItemEntities(entities);
            return order;
        }).collect(Collectors.toList());
        page.setRecords(order_sn);
        return new PageUtils(page);
    }

    /**
     * 处理支付成功返回结果
     * @param vo
     * @return
     */
    @Override
    public String handlePayResult(PayAsyncVo vo) {
        //1、保存交易流水
        PaymentInfoEntity infoEntity = new PaymentInfoEntity();
        infoEntity.setAlipayTradeNo(vo.getTrade_no());
        infoEntity.setAlipayTradeNo(vo.getOut_trade_no());
        infoEntity.setPaymentStatus(vo.getTrade_status());
        infoEntity.setCallbackTime(vo.getNotify_time());
        paymentInfoService.save(infoEntity);

        //2、修改订单状态信息
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")){
            //支付成功状态
            String outTradeNo = vo.getOut_trade_no();
            this.baseMapper.updateOrderStatus(outTradeNo, OrderStatusEnum.PAYED.getCode(), PayConstant.ALIPAY);
        }
        return "success";
    }

    /**
     * 生成一个订单
     * @return
     */
    public OrderCreateTo createOrder(){
        OrderCreateTo createTo = new OrderCreateTo();
        // 1、生成一个订单号
        String orderSn = IdWorker.getTimeId();
        // 创建订单
        OrderEntity orderEntity = buildOrder(orderSn);
        createTo.setOrder(orderEntity);
        // 2、获取所有的订单项
        List<OrderItemEntity> itemEntities = buildOrderItems(orderSn);
        createTo.setOrderItems(itemEntities);
        // 3、计算价格、积分等相关
        computePrice(orderEntity,itemEntities);
        return createTo;
    }

    /**
     * 创建订单
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {
        MemberResponseVO memberResponseVo = LoginUserInterceptor.loginUser.get();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderSn);
        orderEntity.setMemberId(memberResponseVo.getId());
        OrderSubmitVo orderSubmitVo = confirmVoThreadLocal.get();
        // 获取收获地址信息
        R r = wmsFeignService.getFare(orderSubmitVo.getAddrId());
        FareVo fareResp = r.getData(new TypeReference<FareVo>() {
        });
        // 设置运费信息
        orderEntity.setFreightAmount(fareResp.getFare());
        // 设置收货人信息
        orderEntity.setReceiverCity(fareResp.getAddress().getCity());
        orderEntity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
        orderEntity.setReceiverName(fareResp.getAddress().getName());
        orderEntity.setReceiverPhone(fareResp.getAddress().getPhone());
        orderEntity.setReceiverPostCode(fareResp.getAddress().getPostCode());
        orderEntity.setReceiverRegion(fareResp.getAddress().getRegion());
        // 设置订单的相关状态信息
        orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        orderEntity.setAutoConfirmDay(7);
        return orderEntity;
    }

    /**
     * 构建所有订单项数据
     * @return
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 最后确定每个购物项的价格
        List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
        if (currentUserCartItems != null && currentUserCartItems.size()>0){
            List<OrderItemEntity> itemEntities = currentUserCartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
            System.out.println("所有订单数据：" + itemEntities);
            return itemEntities;
        }
        return null;
    }

    /**
     * 构建某一个订单项
     * @param cartItem
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo cartItem) {
        OrderItemEntity orderItemEntity = new OrderItemEntity();
        // 1 订单信息 订单号
        // 2 SPU信息
        Long skuId = cartItem.getSkuId();
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo data = r.getData(new TypeReference<SpuInfoVo>(){});
        orderItemEntity.setSpuId(data.getId());
        orderItemEntity.setSpuBrand(data.getBrandId().toString());
        orderItemEntity.setSpuName(data.getSpuName());
        orderItemEntity.setCategoryId(data.getCatalogId());
        // 3 SKU信息
        orderItemEntity.setSkuId(cartItem.getSkuId());
        orderItemEntity.setSkuName(cartItem.getTitle());
        orderItemEntity.setSkuPic(cartItem.getImage());
        orderItemEntity.setSkuPrice(cartItem.getPrice());
        String skuAttrs = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";"); //将集合转换成字符串
        orderItemEntity.setSkuAttrsVals(skuAttrs);
        orderItemEntity.setSkuQuantity(cartItem.getCount());
        // 4 优惠信息 [不做]

        // 5 积分信息
        orderItemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        orderItemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount().toString())).intValue());
        // 6 订单项的价格信息
        orderItemEntity.setPromotionAmount(new BigDecimal("0"));
        orderItemEntity.setIntegrationAmount(new BigDecimal("0"));
        orderItemEntity.setCouponAmount(new BigDecimal("0"));
        // 当前订单项的实际金额
        BigDecimal origin = orderItemEntity.getSkuPrice().multiply(new BigDecimal(orderItemEntity.getSkuQuantity().toString()));
        // 总额减去各种优惠后的价格
        BigDecimal subtract = origin.subtract(orderItemEntity.getCouponAmount()).subtract(orderItemEntity.getIntegrationAmount()).subtract(orderItemEntity.getPromotionAmount());
        orderItemEntity.setRealAmount(subtract);
        return orderItemEntity;
    }

    /**
     * 计算价格相关
     * @param orderEntity
     * @param itemEntities
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> itemEntities) {
        BigDecimal total = new BigDecimal("0.0");
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");
        // 订单的总额，叠加每一个订单项的总额信息。
        for (OrderItemEntity entity : itemEntities) {
            coupon = coupon.add(entity.getCouponAmount());
            integration = integration.add(entity.getIntegrationAmount());
            promotion = promotion.add(entity.getPromotionAmount());
            total = total.add(entity.getRealAmount());
            gift = gift.add(new BigDecimal(entity.getGiftIntegration().toString()));
            growth = growth.add(new BigDecimal(entity.getGiftGrowth().toString()));
        }
        // 订单价格相关
        orderEntity.setTotalAmount(total);
        // 应付金额
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);
        // 设置积分信息
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());
        // 设置删除状态 0未删除
        orderEntity.setDeleteStatus(0);
    }

    /**
     * 保存订单数据
     * @param order
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);
        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItemService.saveBatch(orderItems);
    }
}