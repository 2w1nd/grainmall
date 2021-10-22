package com.w1nd.grainmall.search;

import com.alibaba.fastjson.JSON;
import com.w1nd.grainmall.search.config.GrainmallElasticSearchConfig;
import lombok.Data;
import org.assertj.core.data.Index;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GrainmallSearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    public void searchData() throws IOException {
        // 1. 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定dsl
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchRequest.source(searchSourceBuilder);
        // 1.1.构造检索条件
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));
        System.out.println(searchSourceBuilder.toString());

        searchRequest.source(searchSourceBuilder);

        // 2. 执行检索
        SearchResponse search = client.search(searchRequest, GrainmallElasticSearchConfig.COMMON_OPTIONS);
        // 3. 分析结果 searchResource

    }

    /**
     * 测试存储数据到es
     */
    @Test
    public void indexData() throws IOException {
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        // indexRequest.source("userName", "w1nd", "age", 8, "gender", "男");
        User user = new User();
        user.setUserName("w1nd");
        user.setAge(18);
        user.setGender("男");
        String s = JSON.toJSONString(user);
        indexRequest.source(s, XContentType.JSON);
        // 执行操作
        IndexResponse index = client.index(indexRequest, GrainmallElasticSearchConfig.COMMON_OPTIONS);
        // 提取有用的数据
        System.out.println(index);
    }

    @Data
    class User {
        private String userName;
        private String gender;
        private Integer age;
    }

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

}
