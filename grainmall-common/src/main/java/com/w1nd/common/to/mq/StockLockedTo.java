package com.w1nd.common.to.mq;

import lombok.Data;

import java.util.List;

@Data
public class StockLockedTo {
    private Long id;    // 库存工作单的id

    private StockDetailTo detailTo; // 工作单详情的id
}
