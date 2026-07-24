该工程采用基于 Spring Boot 的统一异常处理体系，通过枚举错误码、自定义业务异常和全局异常处理器实现标准化的错误响应。

核心架构：ErrorCode 枚举集中管理所有错误码，分为系统级（0-999）和业务级（1001+）两类；ServerException 继承 RuntimeException，用于在业务层抛出结构化异常；GlobalExceptionHandler 使用 @RestControllerAdvice 拦截 ServerException 和其他 Exception，分别记录警告日志和错误堆栈；CommonResult<T> 提供 success() 和 error() 静态工厂方法确保响应格式一致。

安全认证集成：SecurityAutoConfiguration 中配置 authenticationEntryPoint，认证失败时直接返回 UNAUTHORIZED 错误码的 JSON 响应。

使用模式：业务代码通过 throw new ServerException(ErrorCode.XXX) 抛出业务异常，或在配置缺失、第三方服务不可用等场景下使用 throw new ServerException(code, message)。

设计特点：错误码集中管理避免魔法数字；业务异常与系统异常分层处理；全局统一响应格式便于前端处理；安全框架与业务异常处理解耦。