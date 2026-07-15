---
kind: design
name: 采用原生微信小程序替代 uni-app 作为前端实现
source: session
category: adr
---

# 采用原生微信小程序替代 uni-app 作为前端实现

_来源：8bdace8 → 5cb27d2 提交周期内记录的编码计划——内容为规划时意图，实现可能滞后或有出入。_

**状态：** accepted

## 背景
项目原使用 uni-app 骨架，但用户选择直接复制开源商城小程序 `platform-wxshop/wx-mall`（包含 30+ 页面、Banner、购物车、分类、专题、订单等完整功能）到 `shop-miniapp-native/` 目录，以快速获得完整的商城 UI。

## 决策驱动
- 快速获得完整商城 UI
- 减少前端开发工作量
- 直接使用成熟的开源小程序

## 备选方案
- **继续使用 uni-app 框架** _（已否决）_ — 优点：跨平台能力, 与现有代码库一致；缺点：需要从零开发 30+ 页面, 开发周期长
- **迁移到原生微信小程序** — 优点：直接获得完整 UI 和交互逻辑, 无需重复开发, 微信开发者工具调试体验好；缺点：失去跨平台能力, 需要逐步补全后端接口, 维护两套前端代码(uni-app 参考)

## 决策
将 `platform-wxshop/wx-mall/` 完整复制到 `shop-miniapp-native/` 作为主前端, 修改 API 地址指向 Spring Boot 后端 (`http://localhost/app-api/`), 保留 `shop-miniapp/` (uni-app) 作为参考目录后续可删除。

## 影响
前端 UI 完整可用但后端仅实现了商品和分类接口, 其他功能(购物车、订单、登录等)调用会报错需逐步补全; 暂时存在两套前端代码, 增加维护复杂度; 失去了 uni-app 的跨平台能力。