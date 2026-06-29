## 1. 核心框架与工具
- **SLF4J + Logback**: 项目采用 Spring Boot 默认的 SLF4J 门面配合 Logback 实现作为日志底层。
- **Lombok**: 广泛使用 `@Slf4j` 注解在类中自动生成 `log` 实例，简化日志代码编写。

## 2. 关键配置与文件
- **配置文件**: `shop-backend/shop-server/src/main/resources/application-dev.yml` 中定义了日志级别策略。
  - `logging.level.com.shop: debug`: 开发环境下，将业务包 `com.shop` 的日志级别设为 `DEBUG`，便于追踪细节。
- **全局异常处理**: `shop-backend/shop-framework/shop-common/src/main/java/com/shop/common/exception/GlobalExceptionHandler.java` 是日志输出的核心入口之一，统一捕获并记录业务异常（WARN）和系统异常（ERROR）。

## 3. 架构约定与模式
- **分层日志策略**:
  - **WARN**: 用于记录可预期的业务逻辑错误（如 `ServerException`），通常包含错误码和提示信息。
  - **ERROR**: 用于记录未预期的系统级异常（如 `Exception`），并打印完整堆栈信息以便排查。
- **MyBatis SQL 日志**: 在开发环境通过 `mybatis-plus.configuration.log-impl: org.apache.ibatis.logging.stdout.StdOutImpl` 将 SQL 语句直接输出到控制台，方便调试数据访问层。

## 4. 开发者规范
- **注入方式**: 必须使用 `@Slf4j` 注解，禁止手动创建 `LoggerFactory.getLogger()`。
- **占位符使用**: 记录日志时必须使用 `{}` 占位符（如 `log.info("id={}", id)`），禁止字符串拼接，以提升性能。
- **异常记录**: 捕获异常时，必须将异常对象作为最后一个参数传入（如 `log.error("msg", e)`），以确保堆栈信息被正确记录。