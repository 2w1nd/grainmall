package com.w1nd.grainmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.w1nd.common.to.mq.OrderTo;
import com.w1nd.common.to.mq.StockLockedTo;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.grainmall.ware.entity.WareSkuEntity;
import com.w1nd.grainmall.ware.vo.SkuHasStockVo;
import com.w1nd.grainmall.ware.vo.WareSkuLockVo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:56:35
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds);

    /**
     * 锁定库存
     * @param vo
     * @return
     */
    Boolean orderLockStock(WareSkuLockVo vo);

    /**
     * 解锁库存
     * @param to
     */
    void unlockStock(StockLockedTo to);

    /**
     * 解锁订单
     * @param orderTo
     */
    @Transactional(rollbackFor = Exception.class)
    void unlockStock(OrderTo orderTo);
}

