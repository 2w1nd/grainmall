package com.w1nd.grainmall.coupon.service.impl;

import com.w1nd.grainmall.coupon.entity.SeckillSkuRelationEntity;
import com.w1nd.grainmall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.common.utils.Query;

import com.w1nd.grainmall.coupon.dao.SeckillSessionDao;
import com.w1nd.grainmall.coupon.entity.SeckillSessionEntity;
import com.w1nd.grainmall.coupon.service.SeckillSessionService;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    private SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getLasts3DaySession() {
        // 计算最近三天
        LocalDate now = LocalDate.now();
        LocalDate plus = now.plusDays(3);
        List<SeckillSessionEntity> list = this.list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime(), endTime()));
        System.out.println(list);
        if (null != list && list.size() >0){
            List<SeckillSessionEntity> collect = list.stream().map(session -> {
                Long id = session.getId();
                // 查出sms_seckill_sku_relation表中关联的skuId
                List<SeckillSkuRelationEntity> relationEntities = seckillSkuRelationService.list(new QueryWrapper<SeckillSkuRelationEntity>().eq("promotion_session_id", id));
                session.setRelationSkus(relationEntities);
                return session;
            }).collect(Collectors.toList());
            System.out.println("collect:" + collect);
            return collect;
        }

        return null;
    }

    /**
     * 起始时间
     * @return
     */
    private String  startTime(){
        LocalDate now = LocalDate.now();
        LocalTime time = LocalTime.MIN;
        LocalDateTime start = LocalDateTime.of(now, time);

        String format = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

    /**
     * 结束时间
     * @return
     */
    private String endTime(){
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(2);
        LocalTime time = LocalTime.MIN;
        LocalDateTime end = LocalDateTime.of(localDate, time);
        String format = end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return format;
    }

}