本仓库采用 Spring Boot 原生的多环境配置文件机制，通过 application.yml + application-{profile}.yml 的方式管理不同运行环境的配置。

## 1. 系统概览
- 基于 Spring Boot 内置的 Profile 机制，使用 spring.profiles.active 切换环境。
- 所有后端配置集中在 shop-backend/shop-server/src/main/resources/ 下，按环境拆分为多个 YAML 文件。
- 容器化部署时通过 JVM 启动参数 --spring.profiles.active=prod 指定生产环境。

## 2. 核心文件与结构
- application.yml：全局入口，仅声明 spring.profiles.active: dev 和默认端口 8085。
- application-dev.yml：开发环境配置，包含 MySQL 数据源、Redis、MyBatis-Plus 及日志级别等。
- Dockerfile：构建镜像时通过 ENTRYPOINT 注入 --spring.profiles.active=prod，使容器默认加载 application-prod.yml（该文件尚未在仓库中创建）。
- .gitignore 中已排除 .env、.env.local、.env.*.local，表明项目预留了本地环境变量覆盖能力，但当前未启用。

## 3. 架构与约定
- 分层方式：以环境为维度拆分配置，而非以功能模块拆分；每个 profile 对应一个独立 YAML 文件。
- 激活策略：默认激活 dev，生产环境通过命令行参数覆盖，避免将敏感信息硬编码进代码或版本库。
- 数据库与中间件：开发环境直接写死 localhost 连接信息，属于演示阶段做法，生产应通过外部化配置注入。
- 自动装配：ShopServerApplication 仅做包扫描与 MapperScan，不引入自定义 @ConfigurationProperties 类，业务层也未发现 @Value / @ConfigurationProperties 的使用，说明当前配置读取完全依赖 Spring Boot 自动绑定。

## 4. 开发者应遵循的规则
- 新增环境时，复制 application-dev.yml 为 application-{profile}.yml，并在 application.yml 或通过启动参数设置 spring.profiles.active。
- 禁止在代码中使用 @Value 或 @ConfigurationProperties 手动读取配置；统一由 Spring Boot 自动绑定到 Starter 组件。
- 敏感信息不得写入版本库，应通过环境变量或外部配置中心注入。
- 生产环境需补充 application-prod.yml，并确认 Dockerfile 中的 --spring.profiles.active=prod 与实际部署一致。