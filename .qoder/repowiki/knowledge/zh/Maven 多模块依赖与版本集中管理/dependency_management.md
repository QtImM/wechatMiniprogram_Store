本仓库的依赖管理采用 **Maven 多模块聚合工程**，通过父 POM 的 `<dependencyManagement>` 和 `<properties>` 实现第三方库与内部模块版本的统一管控。前后端各自独立管理：后端使用 Maven，前端小程序基于 uni-app 构建（manifest.json 声明平台能力，无 package.json）。

### 1. 系统/工具
- **包管理器**：Maven（Java 生态）
- **版本管理方式**：`<properties>` 集中声明版本号 + `<dependencyManagement>` 统一锁定版本，子模块仅声明 `groupId/artifactId` 不写 version
- **Spring Boot BOM**：通过 `spring-boot-dependencies` 的 `<scope>import</scope>` 引入 Spring 全家桶的传递依赖版本矩阵
- **私有/内部模块**：以 `com.shop:shop-*` 为 groupId，在顶层 `<dependencyManagement>` 中用 `${project.version}` 引用，保证所有子模块版本一致
- **插件版本**：`pluginManagement` 统一管理 `spring-boot-maven-plugin`、`maven-compiler-plugin` 等构建插件版本

### 2. 关键文件
- `shop-backend/pom.xml` — 根 POM，定义 Java/Spring/MyBatis/Hutool/JWT/Lombok 等核心版本，以及内部 starter 模块的版本管理
- `shop-backend/shop-framework/pom.xml` — 框架层聚合 POM，组织 `shop-common`、`shop-starter-web`、`shop-starter-mybatis`、`shop-starter-security` 四个 starter
- `shop-backend/shop-module-product/pom.xml` — 业务模块示例，仅声明对 starter 的依赖，不写版本
- `shop-miniapp/manifest.json` — uni-app 项目清单，声明各平台 appid、SDK 配置及权限，不包含 npm 依赖

### 3. 架构与约定
- **分层依赖模型**：`shop-server` → `shop-module-*` → `shop-starter-*` → `shop-common`，依赖方向自顶向下单向引用，starter 之间互不依赖
- **版本升级路径**：修改根 POM 的 `<properties>` 即可全局升级；升级 Spring Boot 时注意检查 `spring-boot-dependencies` BOM 带来的传递依赖变更
- **Lombok 注解处理**：在 `maven-compiler-plugin` 的 `annotationProcessorPaths` 中显式声明 Lombok，避免 IDE 与 CLI 编译不一致
- **Scope 约定**：Lombok 使用 `provided` scope，确保不会打入最终 JAR
- **前端依赖现状**：小程序未使用 npm 包管理，组件直接放在 `components/` 下（如 `uParse`），属于源码级复用而非包依赖

### 4. 开发者规则
- 新增第三方依赖时，先在根 POM 的 `<properties>` 声明版本号，再在 `<dependencyManagement>` 注册，子模块只写 `groupId/artifactId`
- 内部 starter 模块升级后，所有引用方无需改 version，由 `${project.version}` 自动生效
- 不要在各子模块重复声明已在父 POM 管理的依赖版本，否则将破坏版本一致性
- 如需引入新的 Spring Boot Starter，优先确认其是否已被 `spring-boot-dependencies` BOM 覆盖，避免手动指定版本