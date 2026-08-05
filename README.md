# SKYL1N 的基地（MyWebsite）

## 项目简介

个人主页 / 作品集网站「SKYL1N 的基地」，含欢迎页、首页、关于、作品集、留言等模块。

## 技术栈

| 层 | 技术 |
|---|---|
| 前端 | Vue 3 + Vite（`frontend/`），黑白主题 |
| 后端 | Java 17 + Spring Boot 3.3 + Maven（`backend/`）|
| 数据库 | MySQL 8（库名 `skyl1n_portal`）|
| 部署 | Docker Compose（nginx + 自签证书，`docker-compose.yml`）|

## 目录结构

```
MyWebsite/
├── backend/          # Spring Boot（controller/service/repo 分层）
├── frontend/src/
│   ├── views/        # Splash/Home/About/Projects/Moments/Message/Resume
│   ├── components/   # Navbar/ContactCard/ProjectCard
│   ├── assets/ data/ api/ router/
├── docker-compose.yml # 一键部署
├── 开发日志/          # 每日开发记录
├── 未解决问题.md      # 待办/问题
├── 部署说明.md        # 部署步骤 + 运维命令
└── 问题记录.md        # 历史问题
```

## 本地启动

```bash
# 前端
cd frontend && npm install && npm run dev        # 单前端
npm run dev:all                                   # 前后端同时（需后端）

# 后端
cd backend && mvn spring-boot:run
```

## 版本

- **当前 v1.1.0**（黑白主题大改版），已部署 `https://linyou.cloud`
- 完整部署流程见 `部署说明.md`；版本变更见文末记录

## 文档导航

1. 本文件（项目总览）
2. `部署说明.md`（部署/版本/运维）
3. `未解决问题.md`（待办：移动端适配、https 443）
4. `开发日志/`（最近在做啥，看最新的）
5. `问题记录.md` + `开发技术难题总结.md`（踩过的坑）

## 版本变更记录

| 日期 | 版本 | 内容 |
|---|---|---|
| 2026-08-03 | v1.1.0 | 黑白主题大改版：欢迎页雨夜系统、首页近期经历、联系卡重构；部署 Linux |
| 2026-07-31 | v1.0.1 | 修复访客统计 0/0（crypto.randomUUID 降级）|
| 2026-07-31 | v1.0.0 | 容器化部署 + 全部功能修复 |
