package com.w1nd.grainmall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.w1nd.grainmall.product.service.CategoryBrandRelationService;
import com.w1nd.grainmall.product.vo.Catalog2Vo;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        // 1. ??????????????????id
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
//      1.??????????????????
        List<CategoryEntity> entities = baseMapper.selectList(null);

//      2. ??????????????????????????????
//        ??????????????????
        List<CategoryEntity> level1Menus = entities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0;
        }).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());


        return level1Menus;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO   1.?????????????????????????????????????????????????????????
        baseMapper.deleteBatchIds(asList);
    }

    /**
     * ?????????????????????????????????
     * 1. ??????????????????????????????   @Caching
     * 2. ?????????????????????????????????????????? allEntries = true
     *
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
     * // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ????????????
     * 1. ??????????????????????????????key
     * 2. ????????????????????????????????????
     * 3. ???????????????json??????
     * ?????????RedisCacheConfiguration??????
     * Spring-Cache????????????
     * 1. ????????????
     * ???????????????????????????null????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????  ??????????????? ??????????????????????????????
     * ????????????????????????key???????????? ??????????????????????????????  ????????????
     * 2. ????????????
     * 1. ????????????
     * 2. ??????Canal????????????MySQL???????????????????????????
     * 3. ???????????????????????????????????????
     */
    @Cacheable(value = {"category"}, key = "#root.method.name")  // ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public List<CategoryEntity> getLevel1Categorys() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
        return categoryEntities;
    }

    /**
     * ??????Cacheable??????
     *
     * @return
     */
    @Cacheable(value = {"category"}, key = "#root.method.name")
    public Map<String, List<Catalog2Vo>> getCatalogJson2() {
        /**
         * 1. ???????????????????????????????????????
         */
        List<CategoryEntity> selectList = baseMapper.selectList(null);

        // 1. ????????????1?????????
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);
        // 2. ????????????
        Map<String, List<Catalog2Vo>> parent_cid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            // 1. ??????????????????????????????????????????????????????????????????
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getParentCid());
            // 2. ?????????????????????
            List<Catalog2Vo> catalog2Vos = null;
            if (categoryEntities != null) {
                catalog2Vos = categoryEntities.stream().map(l2 -> {
                    Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName());
                    // 1. ?????????????????????????????????????????????vo
                    List<CategoryEntity> level3Catalog = getParent_cid(selectList, l2.getParentCid());
                    if (level3Catalog != null) {
                        List<Catalog2Vo.Catalog3Vo> collect = level3Catalog.stream().map(l3 -> {
                            // 2. ?????????????????????
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

    // TODO ???????????????????????????OutOfDirectMemoryError
    // ?????????1. springboot2.0??????????????????letture????????????redis?????????????????????netty??????????????????
    // 2. lettuce???bug???????????????????????? -Xmx100m???netty????????????????????????????????????????????? -Xmx300m
    // ????????????-Dio.netty.maxDirectMemory????????????????????????
    // 1. ??????lettuce?????????
    // 2. ??????jedis
    @Override
    public Map<String, List<Catalog2Vo>> getCatalogJson() {
        // ???????????????json?????????????????????json????????????????????????????????????????????????

        /**
         * 1. ????????????????????????????????????
         * 2. ?????????????????????????????????????????????????????????
         * 3. ?????????????????????????????????
         */

        // 1. ??????????????????
        // JSON???????????????????????????
        String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
        if (StringUtils.isEmpty(catalogJSON)) {
            // 2. ?????????????????????????????????
            log.info("??????????????????????????????");
            getCatalogJsonFromDbWithRedisLock();
        }
        // ?????????????????????
        System.out.println("????????????????????????");
        Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON, new TypeReference<Map<String, List<Catalog2Vo>>>() {
        });
        return result;
    }

    /**
     * ???????????????
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedisLock() {
        // 1. ?????????????????????redis??????
        String uuid = UUID.randomUUID().toString();
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 300, TimeUnit.SECONDS);
        if (lock) {
            // ????????????...????????????
            Map<String, List<Catalog2Vo>> dataFromDb = getDataFromDb();

            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del',KEYS[1]) else return 0 end";
            Long lock1 = redisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class), Arrays.asList("lock"), uuid);

            return dataFromDb;
        } else {
            // ????????????
            try {
                Thread.sleep(200);
            } catch (Exception e) {

            }
            return getCatalogJsonFromDbWithRedisLock(); // ??????
        }
    }

    /**
     * redisson??????
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {
        // 1. ?????????????????????redis??????
        // 1. ??????????????????????????????????????????
        // ?????????????????????????????????????????????
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
     * ?????????????????????????????????????????????????????????
     *
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {
        // ??????????????????????????????????????????????????????????????????
        // TODO ????????????synchronized???JUC???Lock???????????????????????????????????????????????????????????????????????????
        synchronized (this) {
            // ?????????????????????????????????????????????????????????????????????????????????????????????
            return getDataFromDb();
        }
    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    private Map<String, List<Catalog2Vo>> getDataFromDb() {
        //?????????????????????????????????????????????????????????????????????????????????????????????
        String catalogJson = redisTemplate.opsForValue().get("catalogJSON");
        if (!StringUtils.isEmpty(catalogJson)) {
            //???????????????????????????
            Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });

            return result;
        }

        System.out.println("??????????????????");

        /**
         * ???????????????????????????????????????
         */
        List<CategoryEntity> selectList = this.baseMapper.selectList(null);

        //1?????????????????????
        //1???1???????????????????????????
        List<CategoryEntity> level1Categorys = getParent_cid(selectList, 0L);

        //????????????
        Map<String, List<Catalog2Vo>> parentCid = level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
            //1???????????????????????????,???????????????????????????????????????
            List<CategoryEntity> categoryEntities = getParent_cid(selectList, v.getCatId());

            //2????????????????????????
            List<Catalog2Vo> catelog2Vos = null;
            if (categoryEntities != null) {
                catelog2Vos = categoryEntities.stream().map(l2 -> {
                    Catalog2Vo catelog2Vo = new Catalog2Vo(v.getCatId().toString(), null, l2.getCatId().toString(), l2.getName().toString());

                    //1????????????????????????????????????????????????vo
                    List<CategoryEntity> level3Catelog = getParent_cid(selectList, l2.getCatId());

                    if (level3Catelog != null) {
                        List<Catalog2Vo.Catalog3Vo> category3Vos = level3Catelog.stream().map(l3 -> {
                            //2????????????????????????
                            Catalog2Vo.Catalog3Vo category3Vo = new Catalog2Vo.Catalog3Vo(l2.getCatId().toString(), l3.getCatId().toString(), l3.getName());

                            return category3Vo;
                        }).collect(Collectors.toList());
                        catelog2Vo.setCatalog3List(category3Vos);
                    }

                    return catelog2Vo;
                }).collect(Collectors.toList());
            }

            return catelog2Vos;
        }));

        //3?????????????????????????????????,???????????????json
        String valueJson = JSON.toJSONString(parentCid);
        redisTemplate.opsForValue().set("catalogJSON", valueJson, 1, TimeUnit.DAYS);

        return parentCid;
    }

    private List<CategoryEntity> getParent_cid(List<CategoryEntity> selectList, Long parent_cid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parent_cid).collect(Collectors.toList());
        return collect;
        // return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", v.getCatId()));
    }

    /**
     * ????????????????????????????????????
     *
     * @param root
     * @param all
     * @return
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
//            ???????????????
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
//            ???????????????
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}