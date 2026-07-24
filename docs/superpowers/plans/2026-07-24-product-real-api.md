# 商品真实接口实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 让既有 `catalog/*` 与 `goods/*` 小程序接口从 MySQL 返回分类、列表与详情数据。

**Architecture:** 新建商品应用查询服务承接分类树、列表筛选和详情组装；新 Controller 保持旧路径与前端字段。Mapper 查询 `product_category`、`product_spu`、`product_sku`，已迁移的 Mock 路由删除。

**Tech Stack:** Java 17、Spring Boot 3.2、MyBatis-Plus、MySQL 8、Maven。

---

### Task 1: 商品数据访问与种子数据

**Files:**
- Create: `shop-backend/shop-module-product/src/main/java/com/shop/module/product/dal/dataobject/ProductSkuDO.java`
- Create: `shop-backend/shop-module-product/src/main/java/com/shop/module/product/dal/mysql/ProductSkuMapper.java`
- Modify: `sql/init.sql`

- [ ] 定义与 `product_sku` 全字段对应的 `ProductSkuDO`，包含 `id`、`spuId`、`properties`、`price`、`marketPrice`、`stock`、`picUrl`、`weight`、`volume`。
- [ ] 新建 `ProductSkuMapper extends BaseMapperX<ProductSkuDO>` 并标注 `@Mapper`。
- [ ] 为 `product_category` 增加二级分类种子数据，为 `product_spu` 增加至少 8 个已上架商品，为每个 SPU 增加至少 1 条 SKU；种子商品必须带主图、轮播图 JSON、简介、详情、价格、库存、销量与排序。
- [ ] 在全新 `shop` 数据库执行 `sql/init.sql`，查询 `SELECT COUNT(*) FROM product_spu;`，预期大于等于 8。

### Task 2: 分类和列表查询服务

**Files:**
- Create: `shop-backend/shop-module-product/src/main/java/com/shop/module/product/service/AppProductQueryService.java`
- Modify: `shop-backend/shop-module-product/src/main/java/com/shop/module/product/dal/mysql/CategoryMapper.java`
- Modify: `shop-backend/shop-module-product/src/main/java/com/shop/module/product/dal/mysql/ProductSpuMapper.java`

- [ ] 先写服务级验证：给定分类树与上架 SPU，`getCatalogIndex()` 返回一级分类及第一个分类详情；`getGoodsList()` 对关键词、分类、新品、热销、分页产生正确总数和记录。
- [ ] 实现 `getCatalogIndex()`、`getCatalogCurrent(Long id)`、`getGoodsCategory(Long id)`；仅查询启用分类，并按 `sort DESC, id ASC` 排序。
- [ ] 实现 `getGoodsCount()` 与 `getGoodsList(categoryId, keyword, isHot, isNew, page, size, sort, order)`；仅返回 `status=1` 的 SPU，将分转为保留两位小数的字符串。
- [ ] 对分类筛选同时匹配所选分类及其子类；关键词匹配 `name`、`keyword`、`introduction`；`isNew=1` 按 `create_time DESC`，`isHot=1` 按 `sales_count DESC`。
- [ ] 运行服务级验证，预期全部通过。

### Task 3: 详情与旧路径 Controller

**Files:**
- Create: `shop-backend/shop-module-product/src/main/java/com/shop/module/product/controller/app/AppGoodsController.java`
- Modify: `shop-backend/shop-module-product/src/main/java/com/shop/module/product/controller/AppMockController.java`

- [ ] 先写 Controller 验证：请求 `/app-api/goods/detail?id=<种子商品ID>` 返回 `info`、`gallery`、`specificationList`、`productList`；请求不存在 ID 返回统一错误。
- [ ] 实现旧路径 `catalog/index`、`catalog/current`、`catalog/{id}`、`goods/category`、`goods/count`、`goods/list`、`goods/detail`、`goods/related`，全部委托 `AppProductQueryService`。
- [ ] 详情根据 `slider_pic_urls` 生成 `gallery`，根据 SKU `properties` 生成 `specificationList` 和 `productList`，关联商品返回同类且排除当前商品的最多 4 条记录。
- [ ] 删除 `AppMockController` 中上述 8 条路由与私有辅助方法，保留未在 #10 范围内的 Mock 路由。
- [ ] 请求新旧页面所用路径，预期 HTTP 200、`code=0` 且无重复映射启动错误。

### Task 4: 联调、状态与提交

**Files:**
- Modify: `docs/superpowers/status.md`

- [ ] 使用 Docker MySQL 导入 `sql/init.sql`，启动 `shop-server`。
- [ ] 通过 `curl` 验证分类、分类筛选列表、关键词列表、新品/热销、分页、详情、关联商品与不存在商品。
- [ ] 使用 `useMock: false` 在小程序验证分类、列表、详情页面；如本机无法运行 HBuilderX，记录后端接口证据。
- [ ] 执行 `cd shop-backend && mvn clean install -DskipTests`，预期 `BUILD SUCCESS`。
- [ ] 将 #10 标记为完成，更新 `status.md` 的阶段记录，执行中文 commit 和 `git push`。
