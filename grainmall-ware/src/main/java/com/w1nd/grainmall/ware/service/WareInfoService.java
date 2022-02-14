package com.w1nd.grainmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.grainmall.ware.entity.WareInfoEntity;
import com.w1nd.grainmall.ware.vo.FareVo;

import java.util.Map;

/**
 * 仓库信息
 *
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 20:56:35
 */
public interface WareInfoService extends IService<WareInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    FareVo getFare(Long addrId);
}

