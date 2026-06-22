# 项目开发规范

> 本文件是项目唯一的规范来源。所有 AI 工具和开发者共同遵守。

---

## 1. Spec-Driven 工作流

| 规则 | 说明 |
|------|------|
| 先读 `docs/superpowers/status.md` | 每次开发第一件事，确认当前位置 |
| 先 spec 后 code | 新功能必须在 spec 中有定义才能动手 |
| Plan 是唯一任务来源 | 不写进 plan 的事不做 |
| 做完即更新 | 任务完成立刻更新 status.md 进度表 |
| 决策即记录 | 架构/技术选择变更写入 status.md 决策记录 |

### 文件结构

```
docs/superpowers/
├── status.md          # 项目状态仪表盘（进度+下一步）
├── specs/             # 设计规格（WHAT to build）
└── plans/             # 实施计划（HOW to build）
```

---

## 2. Git 规范

### 分支策略

```
main          ← 稳定可运行
feat/xxx      ← 功能开发
fix/xxx       ← Bug 修复
```

### Commit Message

```
<类型>: <中文简述>

类型：
  init     - 项目初始化
  feat     - 新功能
  fix      - Bug 修复
  refactor - 重构（不改功能）
  docs     - 文档
  test     - 测试
  chore    - 构建/工具/配置
```

---

## 3. Java 后端规范

### 包结构

```
com.ysy.shop
├── common/          # 公共（CommonResult, 异常, 工具类）
├── module/
│   ├── product/     # 商品模块
│   │   ├── controller/
│   │   ├── service/
│   │   ├── dal/
│   │   │   ├── mapper/
│   │   │   └── dataobject/
│   │   └── vo/
│   ├── member/      # 用户/会员
│   ├── trade/       # 交易/订单
│   └── marketing/   # 营销
└── framework/       # 框架配置（security, mybatis, web）
```

### 命名规则

| 类型 | 规则 | 示例 |
|------|------|------|
| 实体类 | XxxDO | `ProductSpuDO` |
| 请求对象 | XxxReqVO | `ProductCreateReqVO` |
| 响应对象 | XxxRespVO | `ProductListRespVO` |
| Service 接口 | XxxService | `ProductService` |
| Service 实现 | XxxServiceImpl | `ProductServiceImpl` |
| Mapper | XxxMapper | `ProductSpuMapper` |
| Controller | XxxController | `ProductController` |

### API 路径

```
小程序端:  /app/xxx       （前缀 /app）
管理后台:  /admin/xxx     （前缀 /admin）
```

### 统一响应格式

```json
{
  "code": 0,
  "msg": "success",
  "data": { }
}
```

---

## 4. 前端规范（uni-app + Vue3 + TypeScript）

### 目录结构

```
src/
├── pages/           # 页面（一个功能一个文件夹）
├── components/      # 公共组件
├── api/             # 接口定义（按模块分文件）
├── store/           # Pinia 状态管理
├── utils/           # 工具函数
├── types/           # TypeScript 类型
└── static/          # 静态资源
```

### 命名规则

| 类型 | 规则 | 示例 |
|------|------|------|
| 页面文件夹 | kebab-case | `product-detail/` |
| 组件 | PascalCase | `ProductCard.vue` |
| API 文件 | 模块名.ts | `product.ts` |
| Store | use + 模块 + Store | `useCartStore` |

---

## 5. 开发流程

```
1. git pull origin main
2. 读 status.md → 确认当前任务
3. 读对应 plan → 看具体步骤
4. 切分支 feat/task-N-xxx
5. 写代码（按 plan 步骤）
6. 验证（编译通过 / 测试通过）
7. git commit（规范 message）
8. 更新 status.md
9. git push → 合并到 main
```

---

## 6. 禁止事项

- 不写没有对应 spec 的功能
- 不提前优化（先跑通再说）
- 不加用不到的依赖
- 不写空的占位代码（TODO/placeholder）
- 不在代码里硬编码配置（用 application.yml）
- 不跳过验证步骤直接标记完成
