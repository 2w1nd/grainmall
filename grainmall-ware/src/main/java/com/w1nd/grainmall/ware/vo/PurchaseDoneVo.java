package com.w1nd.grainmall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.awt.*;
import java.util.List;

/**
 * @Description:
 * @author: w1nd
 * @date: 2021年09月15日 15:05
 */
@Data
public class PurchaseDoneVo {
    @NotNull
    private Long id; //采购单id

    private List<PurchaseItemDoneVo> items;
}
