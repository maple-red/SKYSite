# ===== 构建阶段 =====
FROM node:18-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm ci --registry=https://registry.npmmirror.com
COPY . .
RUN npm run build

# ===== 运行阶段 =====
FROM nginx:alpine
# 生成自签证书（用于 https→http 跳转）
RUN apk add --no-cache openssl \
 && mkdir -p /etc/nginx/ssl \
 && openssl req -x509 -nodes -newkey rsa:2048 \
      -keyout /etc/nginx/ssl/skyl1n.key \
      -out /etc/nginx/ssl/skyl1n.crt \
      -days 3650 \
      -subj "/CN=skyl1n" \
      -addext "subjectAltName=DNS:localhost,IP:127.0.0.1" \
 && rm /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]
