## 1. 使用的构建系统与技术栈
- 后端采用 Maven 聚合工程，根 shop-backend/pom.xml 声明 5 个子模块（shop-framework、shop-module-product、shop-module-member、shop-module-system、shop-server），统一通过 dependencyManagement 管理 Spring Boot 3.2.5、MyBatis-Plus 3.5.6、Hutool、JWT、Lombok 等依赖版本。
- 前端为 uni-app 微信小程序，使用 HBuilderX / 微信开发者工具编译，项目配置集中在 shop-miniapp/project.config.json，无独立 npm/yarn 脚本。
- 容器化采用 Docker 多阶段构建，基于 maven:3.9-eclipse-temurin-17 执行 mvn package -DskipTests，再以 eclipse-temurin:17-jre-alpine 运行产物。
- 无 Makefile、CI pipeline、发布脚本或 Gradle/NPM 构建流程；container.config.json 仅描述容器运行时参数（端口、扩缩容策略、JVM 内存）供平台消费。

## 2. 关键文件与位置
- shop-backend/pom.xml — 聚合 POM、依赖版本集中管理、pluginManagement 中统一 spring-boot-maven-plugin 与 maven-compiler-plugin 配置。
- shop-backend/Dockerfile — 两阶段构建：build 阶段用 Maven 打包，runtime 阶段以 JRE Alpine 镜像启动，默认激活 prod profile。
- shop-backend/container.config.json — 容器编排元数据（80 端口、CPU/内存、弹性策略、初始延迟）。
- shop-miniapp/project.config.json — uni-app 小程序项目配置（编译器版本、ES6、PostCSS、SourceMap 上传等）。
- sql/init.sql — 数据库初始化脚本，作为部署前置资源。

## 3. 架构与约定
- 多模块分层：shop-framework 提供 starter（web、mybatis、security、common），业务模块 shop-module-* 依赖 starter，shop-server 作为唯一可执行入口，遵循框架层到业务层到应用层的依赖方向。
- 版本治理：所有第三方依赖在根 POM 的 properties 与 dependencyManagement 中统一定义，子模块引用时不写版本号，避免版本漂移。
- 构建产物：最终产出为 shop-server/target/*.jar，Dockerfile 直接拷贝该 jar 并设置 -Xms256m -Xmx512m JVM 参数。
- 环境切换：通过 --spring.profiles.active=prod 注入，配合 application-dev.yml / application.yml 实现多环境配置。
- 小程序构建：由 HBuilderX 或微信开发者工具触发，输出到 unpackage/dist，未纳入 Git 版本控制。

## 4. 开发者应遵守的规则
- 新增依赖必须在根 pom.xml 的 properties 与 dependencyManagement 中声明，子模块仅引入 groupId/artifactId。
- 新增业务模块需在根 POM 的 modules 中注册，并在 shop-server 中按需依赖对应 starter。
- 容器化构建始终通过 mvn package -DskipTests 跳过测试，本地调试请显式执行 mvn test。
- 修改 container.config.json 后需同步更新部署平台的扩缩容策略与资源配额。
- 小程序端变更通过 HBuilderX 编译，不要手动编辑 unpackage/dist 下的生成文件。
- 数据库结构变更应在 sql/init.sql 中维护，并与代码版本一起提交。