## 1. 构建系统概览
本项目采用前后端分离的构建策略，后端基于 **Maven** 进行多模块管理与打包，前端基于 **Vite + uni-app CLI** 进行微信小程序编译。目前项目处于早期开发阶段，缺乏统一的 CI/CD 流水线配置（如 GitHub Actions），主要依赖本地构建与 Docker 容器化部署。

## 2. 后端构建 (shop-backend)
- **工具链**: Maven 3.9 + JDK 17 (Eclipse Temurin)。
- **架构模式**: 模块化单体（Modular Monolith）。根 `pom.xml` 定义了 `shop-framework`（基础设施 Starter）、`shop-module-*`（业务模块）和 `shop-server`（启动入口）。
- **依赖管理**: 通过 `<dependencyManagement>` 统一锁定 Spring Boot 3.2.5、MyBatis-Plus 3.5.6 等核心版本，确保模块间依赖一致性。
- **容器化构建**: 提供 `Dockerfile` 采用多阶段构建（Multi-stage build）：
  1. **Build 阶段**: 使用 `maven:3.9-eclipse-temurin-17` 镜像执行 `mvn package -DskipTests`。
  2. **Run 阶段**: 基于轻量级 `eclipse-temurin:17-jre-alpine` 镜像运行，暴露 80 端口，并指定生产环境 Profile (`--spring.profiles.active=prod`)。

## 3. 前端构建 (shop-miniapp)
- **工具链**: Node.js + Vite + uni-app CLI。
- **脚本命令**:
  - `npm run dev:mp-weixin`: 启动微信小程序开发服务器。
  - `npm run build:mp-weixin`: 编译生成微信小程序生产代码。
- **配置**: 通过 `vite.config.ts` 集成 `@dcloudio/vite-plugin-uni` 插件，实现跨端编译支持。

## 4. 开发规范与协作
- **AI 协作流**: 通过 `.github/copilot-instructions.md` 定义开发工作流，要求每次对话前读取 `docs/superpowers/status.md` 同步进度，并遵守 `docs/conventions.md` 规范。
- **语言约定**: 文档、注释、Commit Message 及沟通均使用中文；代码标识符使用英文。

## 5. 开发者注意事项
- **后端打包**: 修改 `shop-framework` 或公共模块后，需重新安装到本地 Maven 仓库或在根目录执行 `mvn clean install` 以确保依赖更新。
- **环境配置**: 后端默认激活 `prod` profile 运行，本地开发时需手动指定 `--spring.profiles.active=dev` 或调整 `application.yml`。
- **缺失环节**: 当前缺少自动化测试集成（Dockerfile 中跳过测试）及自动化部署脚本，建议后续补充 GitHub Actions 工作流以实现 CI/CD。