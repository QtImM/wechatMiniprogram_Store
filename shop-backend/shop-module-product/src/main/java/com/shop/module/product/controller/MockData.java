package com.shop.module.product.controller;

import java.util.*;

public class MockData {

    public static final List<Map<String, Object>> GOODS_LIST = initGoodsList();

    public static final Map<Long, String> CATEGORY_BANNERS = Map.of(
            1L, "https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=600&auto=format&fit=crop",
            2L, "https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=600&auto=format&fit=crop",
            3L, "https://images.unsplash.com/photo-1534149711956-f9b7d528f64d?w=600&auto=format&fit=crop",
            4L, "https://images.unsplash.com/photo-1616679911721-eff6eec18fcd?w=600&auto=format&fit=crop",
            5L, "https://images.unsplash.com/photo-1514733670139-4d87a19b179d?w=600&auto=format&fit=crop"
    );

    private static List<Map<String, Object>> initGoodsList() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(makeGoodsMap(1, 1, "东阿阿胶糕", "补气养血，美容养颜，传统手工熬制", "99.90", "https://images.unsplash.com/photo-1505252585461-04db1eb84625?w=400", 1, 1));
        list.add(makeGoodsMap(2, 1, "同仁堂枸杞", "宁夏特级免洗枸杞，粒大饱满，甘甜可口", "24.50", "https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=400", 0, 1));
        list.add(makeGoodsMap(3, 1, "长白山人参", "整枝鲜参，长白山道地直供，元气满满", "199.00", "https://images.unsplash.com/photo-1514733670139-4d87a19b179d?w=400", 1, 0));
        list.add(makeGoodsMap(4, 1, "铁皮石斛", "正宗霍山铁皮石斛，胶质浓郁，养阴清热", "159.50", "https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=400", 0, 0));
        list.add(makeGoodsMap(5, 1, "百花蜂蜜", "农家天然土蜂蜜，质地浓稠，蜜香浓郁", "39.90", "https://images.unsplash.com/photo-1587049352846-4a222e784d38?w=400", 1, 0));
        list.add(makeGoodsMap(6, 1, "云南白药三七粉", "超细三七粉，温水冲服，活血化瘀佳品", "39.10", "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?w=400", 0, 1));
        list.add(makeGoodsMap(7, 1, "西洋参切片", "进口花旗参切片，参味浓郁，清热生津", "88.00", "https://images.unsplash.com/photo-1514733670139-4d87a19b179d?w=400", 0, 0));
        list.add(makeGoodsMap(8, 1, "顶级即食燕窝", "东南亚金丝燕盏，低糖零添加，滋阴润燥", "299.00", "https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=400", 1, 1));

        list.add(makeGoodsMap(9, 2, "宁夏枸杞王", "精选大粒贡果，养肝明目，泡水圣品", "49.90", "https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=400", 1, 1));
        list.add(makeGoodsMap(10, 2, "金边玫瑰花茶", "云南墨红玫瑰，花香浓郁，疏肝理气", "28.00", "https://images.unsplash.com/photo-1506084868230-bb9d95c24759?w=400", 1, 0));
        list.add(makeGoodsMap(11, 2, "胎菊王菊花茶", "桐乡特级胎菊，清热明目，汤色金黄", "32.00", "https://images.unsplash.com/photo-1597481499750-3e6b22637e12?w=400", 0, 1));
        list.add(makeGoodsMap(12, 2, "清热金银花茶", "封丘金银花，质地饱满，清热解暑必备", "35.00", "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400", 0, 0));
        list.add(makeGoodsMap(13, 2, "新会小青柑普洱", "正宗新会陈皮与核心产区普洱，醇厚回甘", "58.00", "https://images.unsplash.com/photo-1594631252845-29fc4cc8cfa9?w=400", 0, 0));
        list.add(makeGoodsMap(14, 2, "红枣桂圆双桂茶", "红枣与桂圆气血双补，香甜可口", "29.90", "https://images.unsplash.com/photo-1596701062351-8c2c14d1fdd0?w=400", 1, 1));
        list.add(makeGoodsMap(15, 2, "桑葚干泡茶", "天然无沙桑葚干，花青素爆表，补血滋阴", "26.00", "https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=400", 0, 0));

        list.add(makeGoodsMap(16, 3, "有机黑芝麻丸", "九蒸九晒黑芝麻丸，软糯浓郁，以黑养黑", "39.90", "https://images.unsplash.com/photo-1595855759920-86582396756a?w=400", 0, 1));
        list.add(makeGoodsMap(17, 3, "手剥奶油夏威夷果", "大颗粒奶香夏威夷果，果仁饱满，酥脆香甜", "45.00", "https://images.unsplash.com/photo-1534149711956-f9b7d528f64d?w=400", 1, 1));
        list.add(makeGoodsMap(18, 3, "盐焗原味腰果", "精选大腰果，轻度烘焙，保留坚果原香", "48.00", "https://images.unsplash.com/photo-1600189020840-e9918c25265d?w=400", 0, 0));
        list.add(makeGoodsMap(19, 3, "薄壳手剥碧根果", "临安碧根果，薄壳易剥，果仁香脆", "42.00", "https://images.unsplash.com/photo-1585238342024-78d387f4a707?w=400", 0, 0));
        list.add(makeGoodsMap(20, 3, "东阿阿胶枣", "浓浓阿胶浆浸泡，枣香浓郁，甜而不腻", "18.80", "https://images.unsplash.com/photo-1601004890684-d8cbf643f5f2?w=400", 1, 0));
        list.add(makeGoodsMap(21, 3, "长白山野生松子", "开口开口松子，粒粒甄选，松香四溢", "68.00", "https://images.unsplash.com/photo-1534149711956-f9b7d528f64d?w=400", 0, 1));

        list.add(makeGoodsMap(22, 4, "江中健胃消食片", "消食导滞，用于脾胃虚弱所致的食积", "31.80", "https://images.unsplash.com/photo-1607619056574-7b8f304b3c93?w=400", 0, 1));
        list.add(makeGoodsMap(23, 4, "天然维生素C片", "每片含高活性维C，增强免疫，美白抗氧化", "29.90", "https://images.unsplash.com/photo-1616679911721-eff6eec18fcd?w=400", 0, 0));
        list.add(makeGoodsMap(24, 4, "高钙片", "补钙搭档D3，促进钙吸收，强壮骨骼", "39.90", "https://images.unsplash.com/photo-1584017911766-d451b3d0e843?w=400", 0, 0));
        list.add(makeGoodsMap(25, 4, "益生菌粉", "百亿活菌守护肠道，改善消化，调理肠胃", "59.00", "https://images.unsplash.com/photo-1579758629938-03607ccdbaba?w=400", 1, 0));
        list.add(makeGoodsMap(26, 4, "有机高蛋白营养粉", "动植物双蛋白配合，补充日常营养", "98.00", "https://images.unsplash.com/photo-1593095948071-474c5cc2989d?w=400", 1, 1));

        list.add(makeGoodsMap(27, 5, "经典当归补血汤料包", "传统当归黄芪配方，补气养血，药膳煲汤必备", "45.00", "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=400", 1, 1));
        list.add(makeGoodsMap(28, 5, "正宗四物汤调理包", "当归熟地川芎白芍，调经要药，气血双补", "48.00", "https://images.unsplash.com/photo-1514733670139-4d87a19b179d?w=400", 0, 0));
        list.add(makeGoodsMap(29, 5, "黄金草虫草花", "特级无根虫草花，色泽金黄，汤汁鲜美", "35.00", "https://images.unsplash.com/photo-1563822249548-9a72b6353cd1?w=400", 0, 1));
        list.add(makeGoodsMap(30, 5, "岷县特级当归片", "当归头切片，油性足，参味香浓，煲汤良伴", "28.00", "https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?w=400", 0, 0));
        list.add(makeGoodsMap(31, 5, "甘肃道地黄芪片", "核心产区黄芪，菊花心明显，补气固表", "24.00", "https://images.unsplash.com/photo-1509358271058-acd22cc93898?w=400", 0, 0));
        return list;
    }

    private static Map<String, Object> makeGoodsMap(long id, long categoryId, String name, String brief, String price, String picUrl, int isNew, int isHot) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", id);
        m.put("categoryId", categoryId);
        m.put("name", name);
        m.put("goodsBrief", brief);
        m.put("retailPrice", price);
        m.put("listPicUrl", picUrl);
        m.put("isNew", isNew);
        m.put("isHot", isHot);
        return m;
    }

    public static Map<String, Object> getGoodsById(long id) {
        for (Map<String, Object> g : GOODS_LIST) {
            if (Long.parseLong(g.get("id").toString()) == id) {
                return g;
            }
        }
        // 如果未命中固定列表，按 id 动态生成商品数据，避免全都误展示为“东阿阿胶糕”
        return makeGoodsMap(id, 1, "特色药食商品 #" + id, "道地食材，精选品质", "88.00", "https://images.unsplash.com/photo-1505252585461-04db1eb84625?w=400", 0, 0);
    }
}
