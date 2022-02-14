package com.w1nd.grainmall.order.web;

import com.w1nd.common.exception.NoStockException;
import com.w1nd.grainmall.order.service.OrderService;
import com.w1nd.grainmall.order.vo.OrderConfirmVo;
import com.w1nd.grainmall.order.vo.OrderSubmitVo;
import com.w1nd.grainmall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;


    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        //展示订单确认的数据
        model.addAttribute("orderConfirmData",confirmVo);
        return "confirm";
    }

    /**
     * 下单功能
     * @param submitVo
     * @param model
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo submitVo, Model model, RedirectAttributes redirectAttributes) {
        // 下单 去创建订单 验证令牌 核算价格 锁定库存
        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(submitVo);
            if (responseVo.getCode() == 0) {
                // 下单成功到选择支付方式页面
                model.addAttribute("submitOrderResp", responseVo);
                return "pay";
            } else {
                // 订单失败返回到订单确认页面
                String msg = "下订单失败: ";
                switch (responseVo.getCode()) {
                    case 1 : msg += "订单信息过期, 请刷新后再次提交."; break;
                    case 2 : msg += "订单中的商品价格发生变化, 请刷新后再次提交."; break;
                    case 3 : msg += "库存锁定失败, 商品库存不足."; break;
                }
                // System.out.println(responseVo);
                redirectAttributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.grainmall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                String message =  e.getMessage();
                System.out.println(message);
                redirectAttributes.addFlashAttribute("msg", message);
            }
            // System.out.println("hello");
            return "redirect:http://order.grainmall.com/toTrade";
        }
    }

}
