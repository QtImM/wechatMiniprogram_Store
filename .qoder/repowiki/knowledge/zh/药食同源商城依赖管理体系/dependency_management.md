## 1. 系统与方法
本项目采用 **Maven**（后端）与 **npm/pnpm**（前端小程序）作为核心依赖管理工具。

- **后端 (Java/Spring Boot)**：采用 Maven 多模块聚合架构。通过父 POM (`shop-backend/pom.xml`) 中的 `<dependencyManagement>` 统一管理第三方库版本，确保子模块依赖的一致性。利用 Spring Boot Starter 机制实现基础设施组件的自动装配与解耦。
- **前端 (uni-app/TypeScript)**：基于 `package.json` 进行依赖声明，使用 Vite 作为构建工具。依赖主要围绕 `@dcloudio/uni-*` 生态及 Vue 3/Pinia 状态管理库。

## 2. 关键文件与包
- **后端根配置**：`shop-backend/pom.xml`
  - 定义了全局属性（如 `spring-boot.version: 3.2.5`, `mybatis-plus.version: 3.5.6`）。
  - 引入了 `spring-boot-dependencies` BOM 以对齐 Spring 生态版本。
  - 管理了内部模块（如 `shop-common`, `shop-starter-web`）的版本引用。
- **前端配置**：`shop-miniapp/package.json`
  - 锁定了 `@dcloudio` 系列包的特定版本号（如 `3.0.0-4010120250128001`），确保跨环境构建稳定性。
- **Starter 模块**：
  - `shop-starter-mybatis`: 封装 MyBatis-Plus 配置。
  - `shop-starter-security`: 封装 JWT 认证与安全过滤逻辑。
  - `shop-starter-web`: 封装 Web 基础配置。

## 3. 架构与约定
- **模块化单体设计**：后端划分为 `shop-framework`（基础设施）、`shop-module-*`（业务领域）和 `shop-server`（启动入口）。
- **依赖层级约束**：
  - 业务模块（如 `shop-module-product`）仅依赖 `shop-starter-*`，不直接引入底层框架细节。
  - `shop-server` 负责聚合所有业务模块并打包运行。
- **版本集中化**：所有第三方库版本均在根 POM 的 `<properties>` 中定义，避免“魔术数字”散落在各子模块中。

## 4. 开发者规范
- **新增依赖**：必须在根 `pom.xml` 的 `<dependencyManagement>` 中声明版本，子模块引用时省略 `<version>` 标签。
- **内部模块引用**：统一使用 `${project.version}` 引用同项目下的其他模块，保持版本同步。
- **前端依赖更新**：由于 uni-app 对 CLI 和运行时版本匹配要求严格，更新 `@dcloudio` 相关包时需确保所有子包版本号完全一致。