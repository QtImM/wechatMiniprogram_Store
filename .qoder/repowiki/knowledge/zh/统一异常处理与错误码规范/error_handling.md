## 1. 核心架构与模式

本项目采用 **全局异常处理器 + 统一响应包装** 的模式来处理后端错误，并在前端通过 **请求拦截器** 进行统一的错误反馈。

- **后端 (Spring Boot)**: 使用 `@RestControllerAdvice` 定义全局异常捕获逻辑，将业务异常 (`ServerException`) 和系统异常 (`Exception`) 转换为标准的 `CommonResult` 对象返回给客户端。
- **前端 (uni-app)**: 封装 `request` 函数，根据后端返回的 `code` 字段自动处理成功、未登录（401）及一般业务错误，并通过 `uni.showToast` 提供即时用户反馈。

## 2. 关键组件说明

### 2.1 错误码定义 (`ErrorCode.java`)
采用枚举类管理所有错误码，确保全栈一致性：
- **基础状态码**: `SUCCESS(0)`, `BAD_REQUEST(400)`, `UNAUTHORIZED(401)`, `FORBIDDEN(403)`, `NOT_FOUND(404)`, `INTERNAL_ERROR(500)`。
- **业务错误码**: 从 `1001` 开始递增，例如 `USER_NOT_EXISTS(1001)`, `PRODUCT_NOT_EXISTS(1101)`。

### 2.2 业务异常类 (`ServerException.java`)
继承自 `RuntimeException`，包含 `code` 和 `message` 两个核心属性。支持通过 `ErrorCode` 枚举或自定义 `code/msg` 构造异常实例。

### 2.3 全局异常处理器 (`GlobalExceptionHandler.java`)
- **业务异常处理**: 捕获 `ServerException`，记录 `WARN` 级别日志，并返回对应的错误码和消息。
- **兜底异常处理**: 捕获所有其他 `Exception`，记录 `ERROR` 级别日志及堆栈信息，统一返回 `INTERNAL_ERROR(500)`，防止敏感信息泄露。

### 2.4 统一响应结构 (`CommonResult.java`)
所有接口返回值均包装在此结构中：
```json
{
  "code": 0,
  "msg": "success",
  "data": { ... }
}
```

### 2.5 前端请求封装 (`request.ts`)
- **自动鉴权**: 从本地存储获取 `token` 并注入 `Authorization` 头。
- **状态分流**: 
  - `code === 0`: 解析并返回 `data`。
  - `code === 401`: 清除本地 `token`，提示“请先登录”。
  - 其他错误: 弹出后端返回的 `msg` 提示。

## 3. 开发规范与建议

1. **优先使用枚举**: 在抛出业务异常时，应优先使用 `ErrorCode` 中预定义的枚举值，如 `throw new ServerException(ErrorCode.PRODUCT_NOT_EXISTS);`。
2. **避免直接返回 HTTP 状态码**: 业务逻辑中的错误应通过 `CommonResult` 的 `code` 字段体现，HTTP 状态码通常保持为 200（除非发生严重的网关/框架层错误）。
3. **日志分级**: 业务预期内的错误（如用户不存在）使用 `warn` 级别；非预期的系统崩溃使用 `error` 级别并保留堆栈。
4. **前端错误展示**: 除非有特殊 UI 需求，否则直接复用 `request.ts` 中的默认 Toast 提示，保持用户体验一致。