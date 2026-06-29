package com.shop.module.product.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 分类/专题/购物车等页面 mock 接口 - 开发阶段使用
 */
@RestController
public class AppMockController {

    private static final String IMG = "https://picsum.photos/seed/a1/400/400";

    // =========== 分类页 ===========

    private Map<String, Object> buildCategory(Long id) {
        String name = getCategoryName(id);
        List<Map<String, Object>> subList = List.of(
                Map.of("id", id * 10 + 1, "name", name + "-A", "wapBannerUrl", "https://picsum.photos/seed/cat" + id + "a/200/200"),
                Map.of("id", id * 10 + 2, "name", name + "-B", "wapBannerUrl", "https://picsum.photos/seed/cat" + id + "b/200/200"),
                Map.of("id", id * 10 + 3, "name", name + "-C", "wapBannerUrl", "https://picsum.photos/seed/cat" + id + "c/200/200")
        );
        return Map.of("id", id, "name", name,
                "frontName", name + "精选好物",
                "wapBannerUrl", "https://picsum.photos/seed/cat" + id + "/600/300",
                "subCategoryList", subList);
    }

    @RequestMapping("/app-api/catalog/index")
    public Map<String, Object> catalogIndex() {
        List<Map<String, Object>> categoryList = List.of(
                Map.of("id", 1, "name", "滋补养生"),
                Map.of("id", 2, "name", "茶饮花茶"),
                Map.of("id", 3, "name", "零食坚果"),
                Map.of("id", 4, "name", "保健食品"),
                Map.of("id", 5, "name", "药膳食材")
        );
        return ok(Map.of("categoryList", categoryList, "currentCategory", buildCategory(1L)));
    }

    @RequestMapping("/app-api/catalog/current")
    public Map<String, Object> catalogCurrent(@RequestParam(value = "id", defaultValue = "1") Long id) {
        return ok(Map.of("currentCategory", buildCategory(id)));
    }

    @RequestMapping("/app-api/catalog/{id}")
    public Map<String, Object> catalogDetail(@PathVariable Long id) {
        return ok(Map.of("currentCategory", buildCategory(id)));
    }

    @RequestMapping("/app-api/goods/count")
    public Map<String, Object> goodsCount() {
        return ok(Map.of("goodsCount", 68));
    }

    @RequestMapping("/app-api/goods/category")
    public Map<String, Object> goodsCategory(@RequestParam(value = "id", defaultValue = "1") Long id) {
        List<Map<String, Object>> allCats = List.of(
                Map.of("id", 1, "name", "滋补养生"),
                Map.of("id", 2, "name", "茶饮花茶"),
                Map.of("id", 3, "name", "零食坚果"),
                Map.of("id", 4, "name", "保健食品"),
                Map.of("id", 5, "name", "药膳食材")
        );
        Map<String, Object> current = Map.of("id", id, "name", getCategoryName(id),
                "frontName", getCategoryName(id) + "精选好物");
        return ok(Map.of("brotherCategory", allCats, "currentCategory", current));
    }

    @RequestMapping("/app-api/goods/list")
    public Map<String, Object> goodsList(
            @RequestParam(value = "categoryId", defaultValue = "0") Long categoryId,
            @RequestParam(value = "brandId", defaultValue = "0") Long brandId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        List<String> goodsNames = List.of(
                "东阿阿胶糕", "同仁堂枸杞", "江中健胃消食片", "云南白药三七粉",
                "百花蜂蜜", "宁夏枸杞王", "长白山人参", "铁皮石斛",
                "西洋参切片", "桃胶雪燕羹", "红枣枸杞茶", "玫瑰花茶",
                "菊花枸杞茶", "金银花茶", "陈皮普洱", "红枣桂圆茶",
                "碧根果", "腰果原味", "夏威夷果", "巴日木",
                "维生素C片", "钙片", "益生菌粉", "蛋白粉",
                "当归补血汤", "四物汤料包", "六味地黄丸", "逍遥丸",
                "茯苓饼", "薏米粉", "黑芝麻丸", "阿胶枣",
                "桂花糕", "龟苓膏", "红糖姜茶", "姜糖片",
                "莲子百合汤", "银耳羹", "燕窝", "雪蛤膏",
                "虫草花", "天麻片", "黄芪片", "党参",
                "麦冬", "玉竹", "沙参", "百合干",
                "决明子", "荷叶茶", "绞股蓝", "苦荞茶",
                "红枣", "桂圆干", "桑葚干", "山楂片",
                "罗汉果", "胖大海", "甘草片", "黄芪",
                "党参片", "白术", "苍术", "防风",
                "柴胡", "黄芩", "黄连", "板蓝根",
                "金银花", "连翘", "蒲公英"
        );
        int total = goodsNames.size();
        int pages = (total + size - 1) / size;
        int fromIndex = Math.min((page - 1) * size, total);
        int toIndex = Math.min(fromIndex + size, total);
        List<Map<String, Object>> records = new ArrayList<>();
        for (int i = fromIndex; i < toIndex; i++) {
            double price = 9.9 + (i * 7.3) % 290;
            records.add(Map.of(
                    "id", i + 1,
                    "name", goodsNames.get(i),
                    "listPicUrl", "https://picsum.photos/seed/goods" + (i % 20) + "/200/200",
                    "retailPrice", String.format("%.2f", price)
            ));
        }
        Map<String, Object> goodsList = Map.of(
                "records", records,
                "current", page,
                "size", size,
                "total", total,
                "pages", pages
        );
        return ok(Map.of("goodsList", goodsList, "filterCategoryList", List.of(), "categoryId", categoryId));
    }

    // =========== 商品详情 ===========

    @RequestMapping("/app-api/goods/detail")
    public Map<String, Object> goodsDetail(@RequestParam(value = "id", defaultValue = "1") Long id) {
        String name = "药食同源好物 #" + id;
        String brief = "精选优质原料，传统工艺制作";
        double price = 29.9 + (id * 7.3) % 200;

        // 轮播图
        List<Map<String, Object>> gallery = List.of(
                Map.of("id", 1, "imgUrl", "https://picsum.photos/seed/dg1/600/600"),
                Map.of("id", 2, "imgUrl", "https://picsum.photos/seed/dg2/600/600"),
                Map.of("id", 3, "imgUrl", "https://picsum.photos/seed/dg3/600/600")
        );

        // 商品基本信息
        Map<String, Object> info = Map.of(
                "id", id,
                "name", name,
                "goodsBrief", brief,
                "retailPrice", String.format("%.2f", price),
                "listPicUrl", "https://picsum.photos/seed/dg1/600/600",
                "goodsDesc", "<p>" + name + "，源自道地产区，严格筛选，品质保证。</p>"
        );

        // 品牌
        Map<String, Object> brand = Map.of("id", 1, "name", "药食同源精选");

        // 参数
        List<Map<String, Object>> attribute = List.of(
                Map.of("name", "产地", "value", "中国"),
                Map.of("name", "品牌", "value", "药食同源精选"),
                Map.of("name", "规格", "value", "250g/罐"),
                Map.of("name", "保质期", "value", "24个月"),
                Map.of("name", "储存方式", "value", "密封、阴凉、干燥处")
        );

        // 常见问题
        List<Map<String, Object>> issue = List.of(
                Map.of("id", 1, "question", "如何保存？",
                        "answer", "请密封后放置于阴凉干燥处，避免阳光直射。"),
                Map.of("id", 2, "question", "保质期多久？",
                        "answer", "保质期为24个月，请在有效期内食用。"),
                Map.of("id", 3, "question", "如何食用？",
                        "answer", "可直接食用，也可泡水或煲汤，建议每日10-20g。")
        );

        // 规格列表
        List<Map<String, Object>> specificationList = List.of(
                Map.of("specificationId", 1, "name", "规格",
                        "valueList", List.of(
                                Map.of("id", 1, "value", "250g", "checked", false),
                                Map.of("id", 2, "value", "500g", "checked", false)
                        ))
        );

        // SKU
        List<Map<String, Object>> productList = List.of(
                Map.of("id", 101, "goodsSpecificationIds", "1", "goodsNumber", 99),
                Map.of("id", 102, "goodsSpecificationIds", "2", "goodsNumber", 50)
        );

        // 评价
        Map<String, Object> comment = Map.of(
                "count", 12,
                "data", Map.of(
                        "avatar", "https://picsum.photos/seed/user1/100/100",
                        "nickname", "养生达人",
                        "addTime", "2025-12-01",
                        "content", "品质很好，味道纯正，推荐购买！",
                        "picList", List.of()
                )
        );

        return ok(Map.of(
                "info", info,
                "gallery", gallery,
                "brand", brand,
                "attribute", attribute,
                "issue", issue,
                "specificationList", specificationList,
                "productList", productList,
                "comment", comment,
                "userHasCollect", 0
        ));
    }

    @RequestMapping("/app-api/goods/related")
    public Map<String, Object> goodsRelated(
            @RequestParam(value = "id", defaultValue = "1") Long id) {
        List<Map<String, Object>> goodsList = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            long relatedId = (id + i) % 68 + 1;
            double price = 19.9 + (relatedId * 5.7) % 200;
            goodsList.add(Map.of(
                    "id", relatedId,
                    "name", "推荐好物 #" + relatedId,
                    "listPicUrl", "https://picsum.photos/seed/rel" + i + "/200/200",
                    "retailPrice", String.format("%.2f", price)
            ));
        }
        return ok(Map.of("goodsList", goodsList));
    }

    // =========== 品牌 ===========

    @RequestMapping("/app-api/brand/detail")
    public Map<String, Object> brandDetail(@RequestParam(value = "id", defaultValue = "1") Long id) {
        String name = switch (id.intValue()) {
            case 1 -> "东阿阿胶";
            case 2 -> "同仁堂";
            case 3 -> "江中";
            default -> "品牌" + id;
        };
        Map<String, Object> brand = Map.of(
                "id", id, "name", name,
                "picUrl", "https://picsum.photos/seed/brand" + id + "/600/300",
                "simpleDesc", name + "，品质保证，值得信赖"
        );
        return ok(Map.of("brand", brand));
    }

    // =========== 专题页 ===========

    @RequestMapping("/app-api/topic/list")
    public Map<String, Object> topicList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        List<Map<String, Object>> records = List.of(
                Map.of("id", 1, "title", "药食同源养生指南", "subtitle", "传统中医智慧，现代健康生活",
                        "scenePicUrl", IMG, "priceInfo", "49.9", "content", "药食同源是中国传统养生理念..."),
                Map.of("id", 2, "title", "四季养生茶饮推荐", "subtitle", "顺应时节，调养身心",
                        "scenePicUrl", IMG, "priceInfo", "29.9", "content", "春饮花茶，夏饮绿茶..."),
                Map.of("id", 3, "title", "滋补食材选购攻略", "subtitle", "选对食材，事半功倍",
                        "scenePicUrl", IMG, "priceInfo", "39.9", "content", "如何挑选优质滋补食材...")
        );
        return ok(Map.of("records", records, "total", 3, "pages", 1));
    }

    @RequestMapping("/app-api/topic/detail")
    public Map<String, Object> topicDetail(@RequestParam(value = "id", defaultValue = "1") Long id) {
        return ok(Map.of(
                "id", id,
                "title", "药食同源养生指南",
                "subtitle", "传统中医智慧，现代健康生活",
                "scenePicUrl", IMG,
                "priceInfo", "49.9",
                "content", "<p>药食同源是中国传统养生理念，许多食材同时也是药材。</p><p>合理搭配食材，可以达到调养身心的效果。</p>"
        ));
    }

    @RequestMapping("/app-api/topic/related")
    public Map<String, Object> topicRelated(@RequestParam(value = "id", defaultValue = "1") Long id) {
        List<Map<String, Object>> list = List.of(
                Map.of("id", 2, "title", "四季养生茶饮", "subtitle", "顺应时节",
                        "scenePicUrl", "https://picsum.photos/seed/tr1/400/400", "priceInfo", "29.9"),
                Map.of("id", 3, "title", "滋补食材选购", "subtitle", "选对食材",
                        "scenePicUrl", "https://picsum.photos/seed/tr2/400/400", "priceInfo", "39.9")
        );
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", list);
        return result;
    }

    // =========== 评论 ===========

    @RequestMapping("/app-api/comment/list")
    public Map<String, Object> commentList(
            @RequestParam(value = "valueId", defaultValue = "1") Long valueId,
            @RequestParam(value = "typeId", defaultValue = "0") int typeId,
            @RequestParam(value = "size", defaultValue = "5") int size) {
        List<Map<String, Object>> records = List.of(
                Map.of("id", 1, "nickname", "养生达人", "avatar", "https://picsum.photos/seed/u1/100/100",
                        "addTime", "2025-12-01", "content", "品质很好，推荐！", "picList", List.of()),
                Map.of("id", 2, "nickname", "健康生活", "avatar", "https://picsum.photos/seed/u2/100/100",
                        "addTime", "2025-11-20", "content", "已经回购三次了", "picList", List.of())
        );
        return ok(Map.of("records", records, "total", 2));
    }

    // =========== 购物车 ===========

    @RequestMapping("/app-api/cart/goodscount")
    public Map<String, Object> cartGoodsCount() {
        return ok(Map.of("cartTotal", Map.of("goodsCount", 0)));
    }

    // =========== 我的页面 ===========

    @RequestMapping("/app-api/user/info")
    public Map<String, Object> userInfo() {
        return ok(Map.of(
                "userInfo", Map.of(
                        "nickName", "测试用户",
                        "avatarUrl", IMG,
                        "mobile", "138****8888"
                )
        ));
    }

    // --- helper ---

    private String getCategoryName(Long id) {
        return switch (id.intValue()) {
            case 1 -> "滋补养生";
            case 2 -> "茶饮花茶";
            case 3 -> "零食坚果";
            case 4 -> "保健食品";
            case 5 -> "药膳食材";
            default -> "分类" + id;
        };
    }

    private Map<String, Object> ok(Map<String, Object> data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 0);
        result.put("msg", "success");
        result.put("data", data);
        return result;
    }
}
