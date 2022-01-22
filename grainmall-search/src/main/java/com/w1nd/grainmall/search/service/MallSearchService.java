package com.w1nd.grainmall.search.service;

import com.w1nd.grainmall.search.vo.SearchParam;
import com.w1nd.grainmall.search.vo.SearchResult;

public interface MallSearchService {
    /**
     *
     * @param param 检索的所有参数
     * @return 返回检索的结果
     */
    SearchResult search(SearchParam param);
}
