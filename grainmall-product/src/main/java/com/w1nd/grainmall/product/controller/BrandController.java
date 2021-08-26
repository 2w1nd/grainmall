package com.w1nd.grainmall.product.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.w1nd.common.valid.AddGroup;
import com.w1nd.common.valid.UpdateGroup;
import com.w1nd.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.w1nd.grainmall.product.entity.BrandEntity;
import com.w1nd.grainmall.product.service.BrandService;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author w1nd
 * @email 584202045@qq.com
 * @date 2021-07-22 16:45:31
 */
@RestController
@RequestMapping("product/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
//    @RequiresPermissions("product:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
//    @RequiresPermissions("product:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
//    @RequiresPermissions("product:brand:save")
    public R save(@Validated({AddGroup.class}) @RequestBody BrandEntity brand/*, BindingResult result*/){
//        if (result.hasErrors()) {
//            Map<String, String> map = new HashMap<>();
//            result.getFieldErrors().forEach((item) -> {
////              fielderror获取到错误提示
//                String message = item.getDefaultMessage();
////               获取属性的名字
//                String field = item.getField();
//                map.put(field,message);
//            });
//            return R.error(400, "提交的数据不合法").put("data",map);
//        } else {
            brandService.save(brand);
//        }
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
//    @RequiresPermissions("product:brand:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody BrandEntity brand){
		brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改状态码
     */
    @RequestMapping("/update/status")
//    @RequiresPermissions("product:brand:update")
    public R updateStatus(@Validated({UpdateStatusGroup.class}) @RequestBody BrandEntity brand){
        brandService.updateById(brand);

        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
//    @RequiresPermissions("product:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }

}
