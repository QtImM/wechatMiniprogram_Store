# 药食同源微信小程序商城

微信小程序电商平台，支持实物商品（农副产品+保健品）和虚拟商品（课程研学）。

## 技术栈

- **后端**: Java 17 + Spring Boot 3.2 + MyBatis-Plus 3.5.6 + MySQL 8 + Redis 7
- **小程序**: uni-app (Vue2)（基于开源 wx-mall uni-mall 版本，35 页面）
- **管理后台**: Vue3 + Element Plus（待开发）
- **部署**: 微信云托管（Docker 容器）

## 项目结构

```
wechatMiniprogram_Store/
├── docs/superpowers/              # 项目规格与计划
│   ├── specs/                     # 设计规格
│   ├── plans/                     # 实施计划
│   └── status.md                  # 项目状态仪表盘
├── shop-backend/                  # Java 后端（Maven 多模块）
│   ├── shop-framework/            # 基础框架层
│   │   ├── shop-common/           # 通用工具/常量/异常
│   │   ├── shop-starter-web/      # Web配置/CORS
│   │   ├── shop-starter-mybatis/  # MyBatis-Plus配置/分页
│   │   └── shop-starter-security/ # JWT+Spring Security
│   ├── shop-module-product/       # 商品模块
│   ├── shop-module-member/        # 会员模块
│   ├── shop-module-system/        # 系统模块
│   ├── shop-server/               # 启动入口
│   └── Dockerfile                 # 云托管部署
├── shop-miniapp/                  # uni-app 小程序（Vue2，35 页面）
│   ├── pages/                     # 首页/分类/购物车/我的/专题/商品详情等
│   ├── utils/api.js               # API 接口路径配置
│   ├── utils/util.js              # 工具函数/请求封装
│   ├── static/images/             # 图标资源
│   ├── store/                     # Vuex 状态管理
│   └── pages.json                 # 小程序全局配置
├── sql/                           # 数据库初始化脚本
│   └── init.sql
└── README.md
```

## 开发流程

本项目采用 **Spec-Driven Development**：
1. `status.md` 是项目状态的唯一真相来源
2. 所有功能先写 spec，再写 plan，再编码
3. AI 协作时读取/更新 specs 来保持上下文同步

## 本地测试流程

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0
- Redis 7.x
- [HBuilderX](https://www.dcloud.io/hbuilderx.html)（App开发版，用于编译 uni-app）
- [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)

### 第一步：启动基础服务

```bash
# 启动 MySQL（如果使用 Docker）
docker run -d --name shop-mysql -e MYSQL_ROOT_PASSWORD=root -p 3306:3306 mysql:8.0

# 启动 Redis（如果使用 Docker）
docker run -d --name shop-redis -p 6379:6379 redis:7-alpine
```

### 第二步：初始化数据库

```bash
# 将 SQL 文件复制到容器中并执行（避免字符集问题）
docker exec shop-mysql mysql -u root -proot --default-character-set=utf8mb4 -e "DROP DATABASE IF EXISTS shop;"
docker cp sql/init.sql shop-mysql:/tmp/init.sql
docker exec shop-mysql mysql -u root -proot --default-character-set=utf8mb4 -e "SOURCE /tmp/init.sql;"
```

### 第三步：启动后端服务

```bash
cd shop-backend
# 设置 JAVA_HOME 指向 Java 17（如果默认 Java 版本不是 17）
export JAVA_HOME=/usr/local/Cellar/openjdk@17/17.0.19/libexec/openjdk.jdk/Contents/Home
mvn install -DskipTests -q
mvn spring-boot:run -pl shop-server
```

后端启动成功后监听端口 80。

**验证接口：**

```bash
# 获取商品分类列表
curl http://localhost/app-api/product/category/list

# 创建测试商品
curl -X POST http://localhost/admin-api/product/spu/create \
  -H "Content-Type: application/json" \
  -d '{"categoryId":1,"name":"宁夏枸杞 500g","introduction":"头茬大果粒","picUrl":"https://via.placeholder.com/400","price":5900,"marketPrice":9900,"stock":100,"status":1}'

# 获取商品分页列表
curl http://localhost/app-api/product/spu/page?pageNo=1&pageSize=10
```

### 第四步：启动小程序

1. 下载安装 [HBuilderX](https://www.dcloud.io/hbuilderx.html)（选 **App开发版**，自带 uni-app 编译器）
2. 打开 HBuilderX → 「文件 → 导入 → 从本地目录导入」→ 选择 `shop-miniapp` 目录
3. 首次使用需配置微信开发者工具路径：
   - HBuilderX「设置 → 运行配置 → 微信开发者工具路径」→ 选择微信开发者工具安装目录
   - 同时勾选「启动时打开微信开发者工具」
4. 菜单栏「运行 → 运行到小程序模拟器 → 微信开发者工具」
5. 微信开发者工具自动打开，在「本地设置」中勾选「不校验合法域名」

> HBuilderX 会将 `.vue` 文件编译为微信小程序原生格式，每次保存代码会自动热更新。

> 注意：后端目前已有首页、分类、商品详情等核心 mock 接口，购物车/订单/收藏等接口待补全。

### 测试验证清单

| 验证项 | 预期结果 |
|--------|----------|
| `mvn install -DskipTests` | BUILD SUCCESS |
| 后端启动（端口 80） | 日志无报错 |
| HBuilderX 运行到微信开发者工具 | 编译成功，自动打开模拟器 |
| 小程序首页 | Banner + 分类导航 + 品牌商品 |
| 小程序分类页 | 左侧分类 + 右侧子分类 |
| 小程序商品详情 | 轮播图 + 商品参数 + 常见问题 |
| 小程序购物车 | 页面正常渲染（mock 接口已就绪） |

## 许可证

Private
