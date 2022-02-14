package com.w1nd.grainmall.cart.controller;

import com.w1nd.grainmall.cart.interceptor.CartInterceptor;
import com.w1nd.grainmall.cart.service.CartService;
import com.w1nd.grainmall.cart.service.Impl.CartServiceImpl;
import com.w1nd.grainmall.cart.vo.Cart;
import com.w1nd.grainmall.cart.vo.CartItem;
import com.w1nd.grainmall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 浏览器有一个cookie；user-key:标识用户身份，一个月后过期
     * 如果第一次使用jd购物车功能，都会给一个临时的用户身份
     * 浏览器保存，每次访问都会带上有这个cookies
     *
     * 登录  session有
     * 没登录，按照cookie里面带来的user-key来做
     * 第一次，如果没有临时用户，帮忙创建一个临时用户
     */
    @GetMapping("/cart.html")
    public String cartListPage(Model model) throws ExecutionException, InterruptedException {

        //快速得到用户信息，id,user-key
//        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     * RedirectAttributes attributes
     * attributes.addFlashAttribute();将数据放在session里面可以在页面取出，但只能取一次
     * attributes.addAttribute("skuId",skuId); 将数据放在url后面
     * @return
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId,
                            @RequestParam("num") Integer num,
                            RedirectAttributes attributes) throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        attributes.addAttribute("skuId",skuId);

        return "redirect:http://cart.grainmall.com/addToCartSuccess.html";
    }

    /**
     * 跳转到成功页
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(@RequestParam("skuId") Long skuId,Model model){
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item",cartItem);
        return "success";
    }

    /**
     * 选中
     * @param skuId
     * @param check
     * @return
     */
    @GetMapping("/checkItem")
    public String checkItem(@RequestParam("skuId") Long skuId, @RequestParam("check") Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.grainmall.com/cart.html";
    }

    /**
     * 购买数量修改
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/changeItemCount")
    public String changeItemCount(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num){
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.grainmall.com/cart.html";
    }

    /**
     * 删除购物项
     * @param skuId
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.grainmall.com/cart.html";
    }

    @GetMapping("/currentUserCartItems")
    @ResponseBody
    public List<CartItem> getCurrentUserCartItems(){
        return cartService.getUserCartItems();
    }
}
