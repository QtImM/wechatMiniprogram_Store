## 系统概述
本仓库后端采用 Spring Boot 3.2.5 + Lombok 提供的 `@Slf4j` 注解，通过 SLF4J API 输出日志，底层由 Spring Boot 默认集成的 Logback 驱动。项目未引入独立的 logback.xml 或自定义 Logger 工厂，属于“最小化配置、约定优于配置”的轻量方案。

## 关键文件与位置
- 全局异常处理器：`shop-backend/shop-framework/shop-common/src/main/java/com/shop/common/exception/GlobalExceptionHandler.java`
- 会员认证服务（业务日志示例）：`shop-backend/shop-module-member/src/main/java/com/shop/module/member/service/MemberAuthService.java`
- 微信登录适配服务（外部调用日志示例）：`shop-backend/shop-module-member/src/main/java/com/shop/module/member/service/WxMaService.java`
- 开发环境日志级别配置：`shop-backend/shop-server/src/main/resources/application-dev.yml`（`logging.level.com.shop: debug`）
- 根 POM 中仅声明 Lombok 依赖，SLF4J 由 Spring Boot BOM 管理版本。

## 架构与约定
1. **Logger 注入方式**：所有需要日志的类统一使用 `@Slf4j` 注解，避免手动声明 logger 实例。
2. **日志级别策略**：
   - `info`：记录业务流程关键节点（如用户注册、登录成功、code2session 成功等），并附带核心上下文字段（userId、openid 等）。
   - `warn`：用于可预期的业务异常（如 `ServerException`），在 `GlobalExceptionHandler` 中集中记录。
   - `error`：用于不可恢复的系统异常或外部依赖失败（如微信接口调用失败、网络异常），并携带完整堆栈。
3. **结构化字段**：日志消息采用占位符拼接键值对（如 `[MemberAuth] 新用户注册, userId={}, openid={}`），便于后续按关键字段检索；对外部调用结果也保留原始响应体片段以便排障。
4. **模块隔离**：日志前缀以类名或功能域命名（如 `[MemberAuth]`、`[WxMaService]`），帮助在多模块环境下快速定位来源。
5. **环境差异**：仅在 `application-dev.yml` 中将 `com.shop` 包设为 `debug`，生产环境未显式覆盖，遵循 Spring Boot 默认 `INFO` 级别。
6. **无独立 Sink/异步/采样**：未发现自定义 Appender、RollingFile、ELK/Filebeat 集成或异步日志配置，日志直接输出到控制台（容器场景下由 stdout 收集）。

## 开发者应遵守的规则
- 新增日志一律使用 `@Slf4j`，禁止自行 `LoggerFactory.getLogger(...)`。
- 业务关键路径用 `log.info`，并带上可追踪的标识字段（userId、订单号、openid 等）。
- 可预期的业务错误走 `@RestControllerAdvice` 中的 `warn` 记录，不要吞掉异常也不要在业务层重复打 error。
- 外部依赖失败统一用 `log.error` 并附带异常对象，确保堆栈可见。
- 敏感信息（密码、token、手机号等）不得直接写入日志；如需脱敏应在记录前处理。
- 若需调整日志级别，优先通过 `application-{profile}.yml` 的 `logging.level.*` 覆盖，而非硬编码常量。
- 当前未启用结构化 JSON 日志，如需接入 ELK/日志平台，建议后续引入 `logback-json` 或 Micrometer Logging，保持现有字段风格一致。