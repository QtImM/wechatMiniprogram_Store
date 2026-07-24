# 后端三人并行开发分工

> 目标：把当前“真实登录 + 高保真 Mock Demo”推进为可联调、可验收、可上线的微信小程序商城后端。

---

## 一、当前后端基线

当前后端已经具备：

- Spring Boot 3.2 + Java 17 + MyBatis-Plus 多模块骨架
- 统一响应、统一异常、基础 MyBatis 配置
- Redis Token 服务和微信小程序登录
- 真实微信登录联调通过
- 商品分类、SPU 的基础数据库 CRUD
- 小程序端大量 Mock 接口，可支撑前端演示

当前主要缺口：

- 前端真实调用的 `goods/*`、`cart/*`、`order/*`、`pay/*`、`address/*` 多数仍由 Mock 接口返回
- 数据库目前只有少量基础表，交易、地址、支付、营销、课程等表尚未落地
- `/app-api/**`、`/admin-api/**` 目前开发阶段全放行，鉴权还没有收口
- 后台运营接口只有商品和分类的基础 CRUD，缺少管理员登录、订单、用户、内容、营销管理

---

## 二、三人分工总览

| 开发人员 | 负责方向 | 核心目标 | 推荐优先级 |
|----------|----------|----------|------------|
| 后端 A | 商品与内容真实化 | 首页、分类、列表、详情从 MockData 切到数据库 | 最高 |
| 后端 B | 交易闭环 | 购物车、地址、结算、订单、库存、支付适配 | 最高 |
| 后端 C | 平台基础与运营能力 | 鉴权、后台、会员营销、课程、部署基础 | 中高 |

---

## 三、后端 A：商品与内容真实化

### 目标

让小程序在 `useMock: false` 时，首页、分类页、商品列表、商品详情页全部读取数据库真实数据，不再依赖 `MockData`。

### 负责范围

1. 商品分类真实接口
   - `/app-api/catalog/index`
   - `/app-api/catalog/current`
   - `/app-api/catalog/{id}`
   - `/app-api/goods/category`

2. 商品列表真实接口
   - `/app-api/goods/count`
   - `/app-api/goods/list`
   - `/app-api/goods/hot`
   - `/app-api/goods/new`
   - 支持分类、关键词、新品、热销、分页、排序

3. 商品详情真实接口
   - `/app-api/goods/detail`
   - `/app-api/goods/related`
   - 补齐 SPU、SKU、规格、库存、轮播图、详情富文本、基础评价统计

4. 首页内容真实接口
   - `/app-api/index/banner`
   - `/app-api/index/channel`
   - `/app-api/index/brand`
   - `/app-api/index/topic`
   - `/app-api/index/newGoods`
   - `/app-api/index/hotGoods`
   - `/app-api/index/category`

5. 搜索与收藏最小版本
   - `/app-api/search/index`
   - `/app-api/search/helper`
   - `/app-api/search/clearhistory`
   - `/app-api/collect/addordelete`
   - `/app-api/collect/list`

### 需要补齐的数据表

- `product_sku` 完整落地和查询
- `product_favorite`
- `product_browse_history` 或 `member_footprint`
- `content_banner`
- `content_notice`
- 可选：`product_brand`、`content_topic`

### 验收标准

- 前端 `useMock: false` 时，首页、分类、列表、详情页可正常打开
- 商品图片、名称、价格、库存、规格来自数据库
- 商品列表分页、分类筛选、关键词搜索可用
- 后端 `mvn clean install -DskipTests` 通过

---

## 四、后端 B：交易闭环

### 目标

让用户可以完成“加入购物车 -> 结算 -> 提交订单 -> Mock 支付 -> 查看订单”的完整本地交易链路。

### 负责范围

1. 购物车接口
   - `/app-api/cart/goodscount`
   - `/app-api/cart/index`
   - `/app-api/cart/add`
   - `/app-api/buy/add`
   - `/app-api/cart/update`
   - `/app-api/cart/delete`
   - `/app-api/cart/checked`

2. 地址接口
   - `/app-api/address/list`
   - `/app-api/address/detail`
   - `/app-api/address/save`
   - `/app-api/address/delete`
   - 默认地址逻辑

3. 结算接口
   - `/app-api/cart/checkout`
   - 商品明细、地址、运费、优惠券占位、金额汇总
   - 首期运费规则：固定运费 + 满额包邮

4. 订单接口
   - `/app-api/order/submit`
   - `/app-api/order/list`
   - `/app-api/order/detail`
   - `/app-api/order/cancelOrder`
   - `/app-api/order/confirmOrder`

5. 支付适配
   - `/app-api/pay/prepay`
   - `/app-api/pay/query`
   - 首期保留 Mock 支付参数
   - 建立微信支付 V3 配置入口，等待商户号、证书、APIv3 密钥后切换真实支付

### 需要补齐的数据表

- `trade_cart`
- `member_address`
- `trade_order`
- `trade_order_item`
- `trade_order_logistics`
- `pay_order`
- `pay_refund`
- `trade_freight_template`

### 关键注意事项

- 所有购物车、地址、订单接口必须基于当前登录用户，不允许跨用户读取
- 订单金额以后端计算为准，不能信任前端传入金额
- 提交订单时要校验商品上架状态和库存
- 首期库存扣减可以在提交订单时执行，未支付取消后回补库存
- 后续接微信支付时，支付成功回调必须具备幂等处理

### 验收标准

- 用户登录后可以加购、修改数量、删除、选中商品
- 结算页能显示真实地址、真实商品和真实金额
- 可以提交订单并生成订单号
- Mock 支付后订单状态可更新或查询为已支付
- 订单列表、详情、取消、确认收货可用
- 后端 `mvn clean install -DskipTests` 通过

---

## 五、后端 C：平台基础与运营能力

### 目标

补齐上线和运营所需的基础能力，让后端从 Demo 服务变成可管、可控、可部署的业务系统。

### 负责范围

1. 鉴权与安全收口
   - 调整 Spring Security，不再全量放行 `/app-api/**` 和 `/admin-api/**`
   - 登录、首页、商品浏览等公开接口继续放行
   - 购物车、地址、订单、收藏、用户信息必须登录
   - 管理后台接口必须管理员登录

2. 管理后台后端接口
   - 管理员登录、退出、用户信息
   - 商品管理、分类管理增强
   - 订单列表、订单详情、发货、取消
   - 用户列表、用户详情、禁用/启用
   - Banner、公告、首页推荐位管理

3. 会员与营销最小版本
   - 会员套餐：月卡、年卡
   - 用户会员订阅记录
   - 优惠券模板、用户券、领券、下单用券
   - 满减活动
   - 分享奖励记录和配置

4. 课程模块最小版本
   - 课程主表、章节表
   - 课程列表、详情、购买解锁
   - 学习进度记录

5. 部署与基础设施
   - 生产环境配置项外置
   - Docker 和云托管启动检查
   - 健康检查接口
   - 日志分级和关键业务日志
   - 统一错误码整理

### 需要补齐的数据表

- `sys_admin_user`
- `sys_role`
- `sys_config`
- `member_plan`
- `member_subscription`
- `member_wallet`
- `member_wallet_transaction`
- `promotion_coupon_template`
- `promotion_coupon`
- `promotion_reward_activity`
- `share_record`
- `share_config`
- `course`
- `course_chapter`
- `course_enrollment`
- `course_progress`

### 验收标准

- 管理后台接口不再裸奔，必须管理员登录
- 小程序核心私有接口未登录时返回 `401`
- 运营可以通过后台接口管理商品、分类、订单和首页内容
- 会员、优惠券、课程具备可联调的最小接口
- 生产配置不硬编码敏感信息
- 后端 `mvn clean install -DskipTests` 通过

---

## 六、接口替换原则

当前前端已经固定使用 `shop-miniapp/utils/api.js` 中的接口路径。短期内后端应优先兼容这些路径，避免大规模改前端页面。

优先替换顺序：

1. `goods/*` 和 `index/*`
2. `cart/*` 和 `address/*`
3. `order/*` 和 `pay/*`
4. `collect/*`、`comment/*`、`coupon/*`
5. `brand/*`、`topic/*`、`helpissue/*`

等真实链路稳定后，再逐步整理为更规范的模块路径，例如 `/app-api/product/**`、`/app-api/trade/**`、`/app-api/member/**`。

---

## 七、协作规则

1. 每个开发人员从 `main` 拉新分支开发：
   - `feat/backend-product-real-api`
   - `feat/backend-trade-loop`
   - `feat/backend-platform-admin`

2. 数据库表结构改动必须同步更新：
   - `sql/init.sql`
   - 对应 DO、Mapper、Service、Controller

3. 每个接口完成后至少做到：
   - 返回格式统一为 `{code: 0, msg: "success", data: ...}`
   - 参数校验清楚
   - 登录用户从 SecurityContext 获取
   - 不返回敏感字段

4. 每次合并前必须执行：

```bash
cd shop-backend
mvn clean install -DskipTests
```

5. Commit message 使用中文，例如：

```bash
feat: 完成商品真实列表接口
feat: 完成购物车真实接口
fix: 修复订单金额计算错误
docs: 更新后端三人分工
```

---

## 八、推荐推进顺序

第一轮并行：

- 后端 A：商品真实接口 + 首页真实接口
- 后端 B：购物车 + 地址
- 后端 C：鉴权收口方案 + 管理员登录

第二轮并行：

- 后端 A：搜索、收藏、评论、内容专题
- 后端 B：结算、订单、库存、Mock 支付
- 后端 C：后台商品/订单/用户管理

第三轮并行：

- 后端 A：商品后台增强、SKU 规格完善
- 后端 B：微信支付 V3 接入准备、支付回调幂等
- 后端 C：会员、优惠券、课程、部署上线准备

---

## 九、阶段验收目标

完成本分工第一阶段后，应达到：

- 小程序 `useMock: false` 时，用户可以真实登录
- 首页、分类、商品列表、商品详情来自数据库
- 用户可以加购、结算、提交订单、Mock 支付、查看订单
- 后台具备基础登录和商品/订单管理能力
- 核心私有接口需要登录，不再全部放行

