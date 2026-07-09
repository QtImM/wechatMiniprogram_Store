package com.shop.module.product.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 首页数据接口 - 开发阶段使用模拟数据
 */
@RestController
@RequestMapping("/app-api/index")
public class AppIndexController {

    private static final String IMG = "https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=600&auto=format&fit=crop";
    private static final String IMG2 = "https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=600&auto=format&fit=crop";

    @RequestMapping("/banner")
    public Map<String, Object> banner() {
        List<Map<String, Object>> banners = List.of(
                Map.of("id", 1, "imageUrl", IMG, "link", "/pages/goods/goods?id=1"),
                Map.of("id", 2, "imageUrl", IMG2, "link", "/pages/topic/topic")
        );
        return ok(Map.of("banner", banners));
    }

    @RequestMapping("/channel")
    public Map<String, Object> channel() {
        List<Map<String, Object>> channels = List.of(
                Map.of("id", 1, "name", "新品首发", "iconUrl", "https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=96", "url", "/pages/newGoods/newGoods"),
                Map.of("id", 2, "name", "人气推荐", "iconUrl", "https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=96", "url", "/pages/hotGoods/hotGoods"),
                Map.of("id", 3, "name", "品牌直供", "iconUrl", "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=96", "url", "/pages/brand/brand"),
                Map.of("id", 4, "name", "专题精选", "iconUrl", "https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=96", "url", "/pages/topic/topic"),
                Map.of("id", 5, "name", "全部分类", "iconUrl", "https://images.unsplash.com/photo-1534149711956-f9b7d528f64d?w=96", "url", "/pages/catalog/catalog")
        );
        return ok(Map.of("channel", channels));
    }

    @RequestMapping("/brand")
    public Map<String, Object> brand() {
        List<Map<String, Object>> brands = List.of(
                Map.of("id", 1, "name", "东阿阿胶", "newPicUrl", IMG, "floorPrice", "99.00"),
                Map.of("id", 2, "name", "同仁堂", "newPicUrl", IMG2, "floorPrice", "59.00"),
                Map.of("id", 3, "name", "江中", "newPicUrl", IMG, "floorPrice", "39.00")
        );
        return ok(Map.of("brandList", brands));
    }

    @RequestMapping("/topic")
    public Map<String, Object> topic() {
        List<Map<String, Object>> topics = List.of(
                Map.of("id", 1, "title", "药食同源养生指南", "subtitle", "传统中医智慧，现代健康生活", "scenePicUrl", IMG, "priceInfo", "49.9"),
                Map.of("id", 2, "title", "四季养生茶饮", "subtitle", "顺应时节，调养身心", "scenePicUrl", IMG2, "priceInfo", "29.9")
        );
        return ok(Map.of("topicList", topics));
    }

    @RequestMapping("/newGoods")
    public Map<String, Object> newGoods() {
        List<Map<String, Object>> goods = List.of(
                makeGoods(1),
                makeGoods(9),
                makeGoods(3),
                makeGoods(5)
        );
        return ok(Map.of("newGoodsList", goods));
    }

    @RequestMapping("/hotGoods")
    public Map<String, Object> hotGoods() {
        List<Map<String, Object>> goods = List.of(
                makeHotGoods(1),
                makeHotGoods(22),
                makeHotGoods(16)
        );
        return ok(Map.of("hotGoodsList", goods));
    }

    @RequestMapping("/category")
    public Map<String, Object> category() {
        List<Map<String, Object>> floorGoods = List.of(
                Map.of("id", 1, "name", "滋补养生", "goodsList", List.of(
                        makeGoods(1),
                        makeGoods(2)
                )),
                Map.of("id", 2, "name", "茶饮花茶", "goodsList", List.of(
                        makeGoods(10),
                        makeGoods(11)
                )),
                Map.of("id", 3, "name", "零食坚果", "goodsList", List.of(
                        makeGoods(16),
                        makeGoods(17)
                ))
        );
        return ok(Map.of("categoryList", floorGoods));
    }

    // --- helper ---

    private Map<String, Object> makeGoods(long id) {
        Map<String, Object> goods = MockData.getGoodsById(id);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", goods.get("name"));
        m.put("listPicUrl", goods.get("listPicUrl"));
        m.put("retailPrice", goods.get("retailPrice"));
        return m;
    }

    private Map<String, Object> makeHotGoods(long id) {
        Map<String, Object> goods = MockData.getGoodsById(id);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", goods.get("name"));
        m.put("listPicUrl", goods.get("listPicUrl"));
        m.put("retailPrice", goods.get("retailPrice"));
        m.put("goodsBrief", goods.get("goodsBrief"));
        return m;
    }

    private Map<String, Object> ok(Map<String, Object> data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", data);
        return result;
    }
}
