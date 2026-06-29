package com.shop.module.product.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 首页数据接口 - 开发阶段使用模拟数据
 */
@RestController
@RequestMapping("/app-api/index")
public class AppIndexController {

    private static final String IMG = "https://picsum.photos/seed/a1/400/400";
    private static final String IMG2 = "https://picsum.photos/seed/b3/400/400";

    @RequestMapping("/banner")
    public Map<String, Object> banner() {
        List<Map<String, Object>> banners = List.of(
                Map.of("id", 1, "imageUrl", IMG, "link", "/pages/goods/goods?id=1"),
                Map.of("id", 2, "imageUrl", IMG, "link", "/pages/topic/topic")
        );
        return ok(Map.of("banner", banners));
    }

    @RequestMapping("/channel")
    public Map<String, Object> channel() {
        List<Map<String, Object>> channels = List.of(
                Map.of("id", 1, "name", "新品首发", "iconUrl", "https://picsum.photos/seed/c1/96/96", "url", "/pages/newGoods/newGoods"),
                Map.of("id", 2, "name", "人气推荐", "iconUrl", "https://picsum.photos/seed/c2/96/96", "url", "/pages/hotGoods/hotGoods"),
                Map.of("id", 3, "name", "品牌直供", "iconUrl", "https://picsum.photos/seed/c3/96/96", "url", "/pages/brand/brand"),
                Map.of("id", 4, "name", "专题精选", "iconUrl", "https://picsum.photos/seed/c4/96/96", "url", "/pages/topic/topic"),
                Map.of("id", 5, "name", "全部分类", "iconUrl", "https://picsum.photos/seed/c5/96/96", "url", "/pages/catalog/catalog")
        );
        return ok(Map.of("channel", channels));
    }

    @RequestMapping("/brand")
    public Map<String, Object> brand() {
        List<Map<String, Object>> brands = List.of(
                Map.of("id", 1, "name", "东阿阿胶", "newPicUrl", IMG, "floorPrice", "99.00"),
                Map.of("id", 2, "name", "同仁堂", "newPicUrl", IMG, "floorPrice", "59.00"),
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
                makeGoods(1, "阿胶糕 500g", "99.90"),
                makeGoods(2, "枸杞茶 250g", "49.90"),
                makeGoods(3, "黑芝麻丸", "39.90"),
                makeGoods(4, "红枣夹核桃", "29.90")
        );
        return ok(Map.of("newGoodsList", goods));
    }

    @RequestMapping("/hotGoods")
    public Map<String, Object> hotGoods() {
        List<Map<String, Object>> goods = List.of(
                makeHotGoods(1, "阿胶糕 500g", "99.90", "补气养血，美容养颜"),
                makeHotGoods(2, "枸杞茶 250g", "49.90", "滋补肝肾，明目润肺"),
                makeHotGoods(3, "黑芝麻丸", "39.90", "乌发养颜，补肾益精")
        );
        return ok(Map.of("hotGoodsList", goods));
    }

    @RequestMapping("/category")
    public Map<String, Object> category() {
        List<Map<String, Object>> floorGoods = List.of(
                Map.of("id", 1, "name", "滋补养生", "goodsList", List.of(
                        makeGoods(1, "阿胶糕 500g", "99.90"),
                        makeGoods(2, "枸杞茶 250g", "49.90")
                )),
                Map.of("id", 2, "name", "茶饮花茶", "goodsList", List.of(
                        makeGoods(3, "红枣枸杞茶", "35.00"),
                        makeGoods(4, "菊花茶", "28.00")
                )),
                Map.of("id", 3, "name", "零食坚果", "goodsList", List.of(
                        makeGoods(5, "黑芝麻丸", "39.90"),
                        makeGoods(6, "红枣夹核桃", "29.90")
                ))
        );
        return ok(Map.of("categoryList", floorGoods));
    }

    // --- helper ---

    private Map<String, Object> makeGoods(long id, String name, String price) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("listPicUrl", IMG);
        m.put("retailPrice", price);
        return m;
    }

    private Map<String, Object> makeHotGoods(long id, String name, String price, String brief) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("listPicUrl", IMG);
        m.put("retailPrice", price);
        m.put("goodsBrief", brief);
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
