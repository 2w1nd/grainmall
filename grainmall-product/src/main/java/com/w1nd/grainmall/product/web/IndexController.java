package com.w1nd.grainmall.product.web;

import com.w1nd.grainmall.product.entity.CategoryEntity;
import com.w1nd.grainmall.product.service.CategoryService;
import com.w1nd.grainmall.product.vo.Catalog2Vo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redisson;

    @Autowired
    StringRedisTemplate redisTemplate;

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

    @ResponseBody
    @GetMapping("/hello")
    public String hello() throws InterruptedException {
        // 1. 获取一把锁，只要锁的名字一样，就是同一吧锁
        RLock lock = redisson.getLock("my-lock");
        // 2. 加锁
        // 1) , 锁的自动续期，如果业务超长，运行期间自动给锁续上新的30s，不用担心业务时间长，锁自动过期被删掉
        // 2) , 加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后自动删除
        lock.lock(10, TimeUnit.SECONDS);
        // 问题：lock.lock()，在锁时间到了以后，不会自动续期
        // 1. 如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
        // 2. 如果未指定锁的超时时间，就是用30 * 1000【看门狗的默认时间】
        //  只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】，只要业务在执行，就会在10s后进行续期
        try {
            System.out.println("加锁成功，执行业务。。。。" + Thread.currentThread().getId());
            Thread.sleep(3000);
        } finally {
            // 3. 解锁，将
            System.out.println("释放锁。。。" + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    // 保证一定能读到最新数据，修改期间，写锁是一个排他锁，读锁是一个共享锁
    @GetMapping("/write")
    @ResponseBody
    public String writeValue() {
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String s = "";
        RLock rLock = lock.writeLock();
        try {
            // 1. 改数据加写锁，读数据加读锁
            rLock.lock();
            System.out.println("写锁加锁成功。。。" + Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue", s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
            System.out.println("写锁释放" + Thread.currentThread().getId());
        }
        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readValue() {
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        String s = "";
        // 加读锁
        RLock rLock = lock.readLock();
        rLock.lock();
        try {
            s = redisTemplate.opsForValue().get("writeValue");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    @ResponseBody
    @GetMapping("/hello1")
    public String hello1() {
        return "hello1";
    }
}
