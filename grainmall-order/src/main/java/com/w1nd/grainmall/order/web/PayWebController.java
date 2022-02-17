package com.w1nd.grainmall.order.web;

import com.alipay.api.AlipayApiException;
import com.w1nd.grainmall.order.config.AlipayTemplate;
import com.w1nd.grainmall.order.service.OrderService;
import com.w1nd.grainmall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayWebController {
    @Autowired
    AlipayTemplate alipayTemplate;

    @Autowired
    OrderService orderService;

    /**
     * 1. 将支付页展示
     * 2. 回调页面展示
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

//        PayVo payVo = new PayVo();
//        payVo.setBody();//订单备注
//        payVo.setOut_trade_no();//订单号
//        payVo.setSubject();//订单主题
//        payVo.setTotal_amount();//订单金额
        PayVo payVo = orderService.getOrderPay(orderSn);
        //返回的是一个页面。将此页面直接交给浏览器就行
        String pay = alipayTemplate.pay(payVo);
        System.out.println(pay);
        return pay;
    }
}
