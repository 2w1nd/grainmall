package com.w1nd.grainmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.w1nd.grainmall.product.service.CategoryBrandRelationService;
import com.w1nd.grainmall.product.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.w1nd.common.utils.PageUtils;
import com.w1nd.common.utils.Query;

import com.w1nd.grainmall.product.dao.CategoryDao;
import com.w1nd.grainmall.product.entity.CategoryEntity;
import com.w1nd.grainmall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

//    @Autowired
//    CategoryDao categoryDao;

    @Autowired
    CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redisson;

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();

        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);

        return paths.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        // 1. 收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
//      1.查出所有分类
        List<CategoryEntity> entities = baseMapper.selectList(null);

//      2. 组装成父子的树形结构
//        找到一级分类
        List<CategoryEntity> level1Menus = entities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0;
        }).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO   1.检查当前删除的菜单，是否被别的地方引用
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * 级联更新所有关联的数据
     * 1. 同时进行多种缓存操作   @Caching
     * 2. 指定删除某个分区下的所有数据 allEntries = true
     * @param category
     */
    // @Caching(evict = {
    //         @CacheEvict(value = "category", key = "'getLevel1Categorys'"),
    //         @CacheEvict(value = "category", key = "'getCatalogJson'")
    // })
    @CacheEvict(value = "category", allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     // 每一个需要缓存的数据我们都要指定要放到哪个名字的缓存【缓存的分区（按照业务类型分）】
     *  自定义：
     *  1. 指定生成的缓存使用的key
     *  2. 指定缓存的数据的存活时间
     *  3. 将数据存为json格式
     */
    @Cacheable(value = {"category"}, key = "#root.method.name")  // 代表当前方法的结果需要缓存，如果缓存中有，方法不用调用，如果缓存中没有，会调用方法，最后将方法
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * 使用Cacheable实现
     * @return
     */
    @Cacheable(value = {"category"}, key = "#root.method.name")
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        /**
         * 1. 将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1. 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        // 2. 封装数据
        Map<String, List<Catalog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 1. 每一个的一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getParentCid());
            // 2. 封装上面的结果
            List<Catalog2Vo> catalog2Vos = null;
            if (categoryEntities != null) {
                catalog2Vos = categoryEntities.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 1. 找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catalog = getParent_cid(selectList, l2.getParentCid());
                    if (level3Catalog != null) {
                        List<Catalog2Vo.Catalog3Vo> collect = level3Catalog.stream().map(l3 -> {
                            // 2. 封装成指定格式
                            Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(collect);
                    }
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));

        return parent_cid;
    }
    // TODO 产生对外内存溢出：OutOfDirectMemoryError
    // 原因：1. springboot2.0以后默认使用letture作为操作redis的客户端。使用netty进行网络通信
    // 2. lettuce的bug导致堆外内存溢出 -Xmx100m：netty如果没有指定堆外内存，默认使用 -Xmx300m
    // 可以通过-Dio.netty.maxDirectMemory只会调大堆外内存
    // 1. 升级lettuce客户端
    // 2. 使用jedis
    public Map<String, List<Catalog2Vo>> getCatalogJson2() {
        // 给缓存中放json字符串，拿出的json字符串，还要逆转为能用的对象类型

        /**
         * 1. 空结果缓存：解决缓存穿透
         * 2. 设置过期时间（加随机值）：解决缓存雪崩
         * 3. 加锁：解决缓存击穿问题
         */

        // 1. 加入缓存逻辑
        // JSON跨语言，跨平台兼容
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            // 2. 缓存中没有，查询数据库
            // System.out.println("缓存不命中，查询数据库.....");
            getCatalogJsonFromDbWithRedisLock();
        }
        // 转为指定的对象
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
        return result;
    }

    /**
     * redisson加锁
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        // 1. 占分布式锁，去redis占坑
        // 1. 锁的名字。锁的粒度，越细越快
        // 锁的粒度：具体缓存的是某个数据
        RLock lock = redisson.getLock("catalogJson-lock");
        lock.lock();

        Map<String, List<Catalog2Vo>> dataFromDb;
        try {
            dataFromDb = getDataFromDb();
        } finally {
            lock.unlock();
        }
        return dataFromDb;
    }


    /**
     * 分布式加锁
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        // 1. 占分布式锁，去redis占坑
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            // 加锁成功...执行业务
            Map<String, List<Catalog2Vo>> dataFromDb = getDataFromDb();

            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);

            return dataFromDb;
        } else {
            // 加锁失败
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            return getCatalogJsonFromDbWithRedisLock(); // 自旋
        }
    }



    /**
     * 从数据库查询并封装分类数据（本地加锁）
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {
        // 只要是同一把锁，就能锁住需要这个锁的所有线程
        // TODO 本地锁：synchronized，JUC（Lock），在分布式情况下，想要锁住所有，必须使用分布式锁
        synchronized (this) {
            // 得到锁之后，我们应该再去缓存中确定一次，如果没有才需要继续查询
            return getDataFromDb();
        }
    }

    /**
     * 从数据库中获取数据
     * @return
     */
    private Map<String, List<Catalog2Vo>> getDataFromDb() {
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJSON)) {
            // 缓存为不为null直接返回
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
            return result;
        }
        /**
         * 1. 将数据库的多次查询变为一次
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1. 查出所有1级分类
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        // 2. 封装数据
        Map<String, List<Catalog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 1. 每一个的一级分类，查到这个一级分类的二级分类
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getParentCid());
            // 2. 封装上面的结果
            List<Catalog2Vo> catalog2Vos = null;
            if (categoryEntities != null) {
                catalog2Vos = categoryEntities.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 1. 找当前二级分类的三级分类封装成vo
                    List<CategoryEntity> level3Catalog = getParent_cid(selectList, l2.getParentCid());
                    if (level3Catalog != null) {
                        List<Catalog2Vo.Catalog3Vo> collect = level3Catalog.stream().map(l3 -> {
                            // 2. 封装成指定格式
                            Catalog2Vo.Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());
                            return catalog3Vo;
                        }).collect(Collectors.toList());
                        catalog2Vo.setCatalog3List(collect);
                    }
                    return catalog2Vo;
                }).collect(Collectors.toList());
            }
            return catalog2Vos;
        }));

        // 3. 将查到的数据再放入缓存，将对象转为json放入缓存中
        String s = JSON.toJSONString(parent_cid);
        redisTemplate.opsForValue().set("catalogJSON", s, 1, TimeUnit.DAYS);
        return parent_cid;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
        // return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
    }

    /**
     * 递归查找所有菜单的子菜单
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
//            找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
//            菜单的排序
            return (menu1.getSort()==null?0:menu1.getSort()) - (menu2.getSort()==null?0:menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}