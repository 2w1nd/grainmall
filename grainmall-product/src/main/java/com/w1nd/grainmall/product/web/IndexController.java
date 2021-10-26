package com.w1nd.grainmall.product.web;

import com.w1nd.grainmall.product.entity.CategoryEntity;
import com.w1nd.grainmall.product.service.CategoryService;
import com.w1nd.grainmall.product.vo.Catalog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({"/", "index.html"})
    public String indexPage(Model model) {

        // 1. 查出所有的1级分类
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();
        // 视图解析器进行拼串
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        Map<String, List<Catalog2Vo>> catalogJson = categoryService.getCatalogJson();
        return catalogJson;
    }
}
