## 1. 核心系统与工具
本项目采用 **Spring Boot** 原生的配置管理体系，结合 **YAML** 格式进行分层配置。后端通过自定义 **Starter（启动器）** 模式实现基础设施的“约定优于配置”，前端则依赖构建工具（Vite）和环境常量进行简单的 API 地址配置。

## 2. 关键文件与逻辑分布
### 后端 (shop-backend)
- **主配置文件**：`shop-server/src/main/resources/application.yml`
  - 负责全局基础配置，如激活的环境 `spring.profiles.active: dev` 和服务器端口。
- **环境配置文件**：`shop-server/src/main/resources/application-dev.yml`
  - 存储开发环境的敏感信息，包括 MySQL 数据库连接、Redis 地址以及 MyBatis-Plus 的全局策略（如逻辑删除字段、下划线转驼峰）。
- **自动装配类**：位于 `shop-framework` 下的各个 Starter 模块。
  - `MybatisAutoConfiguration.java`：通过 `@Bean` 注入分页插件和元数据处理器，统一了 ORM 行为。
  - `SecurityAutoConfiguration.java`：定义了 Spring Security 的过滤链，配置了公开接口（如认证端点）和受保护接口的权限规则。
  - `WebAutoConfiguration.java`：实现了 `WebMvcConfigurer`，全局配置了跨域资源共享（CORS）策略。

### 前端 (shop-miniapp)
- **构建配置**：`vite.config.ts`
  - 目前仅包含基础的 uni-app 插件配置，未涉及复杂的环境变量注入。
- **API 请求封装**：`src/api/request.ts`
  - 硬编码了 `BASE_URL`（当前为 `http://localhost:80`），并在请求头中动态注入从本地存储获取的 Token。

## 3. 架构设计与约定
- **模块化单体配置解耦**：通过将通用配置（如 Web、Security、MyBatis）抽取到独立的 `shop-starter-*` 模块中，业务模块（如 `shop-module-product`）无需关心底层基础设施的配置细节，只需引入对应的 Starter 即可自动生效。
- **多环境隔离**：利用 Spring Profile 机制，将不同环境（dev, prod 等）的差异性配置（如数据库密码、日志级别）隔离在独立的 YAML 文件中，确保生产环境的安全性。
- **安全配置集中化**：所有的鉴权逻辑、白名单路径以及异常处理入口均集中在 `SecurityAutoConfiguration` 中，避免了在各个 Controller 中重复编写安全代码。

## 4. 开发者规范与建议
- **敏感信息管理**：严禁将生产环境的数据库密码或密钥直接提交到 `application-*.yml`。建议在实际部署时通过环境变量（Environment Variables）覆盖 YAML 中的配置项。
- **前端 API 地址管理**：目前前端的 `BASE_URL` 为硬编码。建议在 `vite.config.ts` 中定义 `process.env` 变量，或在 `manifest.json` 中配置不同平台的代理，以支持开发与生产环境的无缝切换。
- **配置扩展原则**：若需新增全局功能（如全局异常处理、统一的响应体封装），应优先在 `shop-framework` 中创建新的 Starter 模块，并通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 进行注册，保持业务代码的纯净。