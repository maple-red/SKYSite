/**
 * 项目数据
 * 描述简洁务实，不夸张
 */

/**
 * 根据项目 ID 生成渐变色封面
 */
const gradients = [
  'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
  'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)'
]

export function getProjectCover(project) {
  // 默认使用 picsum 生成的随机项目图；显式 gradient 才用渐变色
  if (project.coverType === 'gradient') {
    return gradients[(project.id - 1) % gradients.length]
  }
  return `https://picsum.photos/seed/${project.id}/400/240`
}

export const projects = [
  {
    id: 1,
    title: 'e舍宿连',
    summary: '面向高校的双角色宿舍管理平台，实现住宿管理、报修处理等核心业务',
    tags: ['Spring Boot', 'Vue3', 'MyBatis', 'JWT'],
    detail: '面向高校的双角色（管理员/学生）宿舍管理平台，实现住宿管理、报修处理、调/退宿申请、房间状态追踪、室友信息查询等核心业务。前后端分离架构，支持多主题与中英文切换。',
    role: '全栈开发',
    challenges: [
      'Spring Security + JWT 无状态认证，BCrypt 加密存储',
      'Vue Router 路由守卫根据角色动态生成权限路由',
      '多主题与中英文切换，localStorage 持久化偏好设置',
      'Druid 连接池 + SQL 监控与慢查询日志'
    ],
    github: 'https://github.com/maple-red'
  },
  {
    id: 2,
    title: '校园帮帮递',
    summary: '基于 Spring Cloud 的校园快递代取服务平台',
    tags: ['Spring Cloud', 'Vue3', 'Docker'],
    detail: '校园快递代取服务平台，支持用户发布代取订单、接单、配送跟踪等功能。微服务架构设计，涵盖服务注册发现、配置中心、网关路由等功能。',
    role: '全栈开发',
    challenges: ['微服务拆分与通信', '分布式事务处理', '服务熔断与降级'],
    github: 'https://github.com/maple-red'
  },
  {
    id: 3,
    title: '天空论坛系统',
    summary: '基于 Spring Boot 的校园技术交流论坛',
    tags: ['Spring Boot', 'Vue3', 'MySQL'],
    detail: '校园技术交流论坛，支持发帖、评论、点赞、关注等社交功能。集成 Elasticsearch 实现全文检索。',
    role: '全栈开发',
    challenges: ['Elasticsearch 全文检索集成', '热帖权重算法', 'Redis 缓存加速'],
    github: 'https://github.com/maple-red'
  },
  {
    id: 4,
    title: '地震数据分析',
    summary: 'Python 爬虫 + 数据分析可视化项目',
    tags: ['Python', '爬虫', 'Pandas'],
    detail: '爬取中国地震台网数据并进行清洗分析，使用 Pandas 进行数据处理，可视化展示地震分布与趋势。',
    role: '独立开发',
    challenges: ['反爬策略处理', '数据清洗与规范化', '可视化呈现'],
    github: 'https://github.com/maple-red/Crawlweb'
  },
  {
    id: 5,
    title: '电话本系统',
    summary: '基于 Linux 环境的联系人管理工具',
    tags: ['Linux', 'Java', 'Shell'],
    detail: '简单的电话本管理系统，支持联系人增删改查，部署至 Linux 环境运行。',
    role: '独立开发',
    challenges: ['Linux 环境适配', '文件 I/O 持久化'],
    github: 'https://github.com/maple-red/Contact'
  },
  {
    id: 6,
    title: '王者官网仿写',
    summary: '大一下学期仿照王者荣耀官网制作的前端页面',
    tags: ['HTML5', 'CSS3', 'JavaScript'],
    detail: '仿照王者荣耀官方网站制作的前端静态页面，练习 HTML5 + CSS3 布局与 JS 交互。',
    role: '独立开发',
    challenges: ['复杂布局还原', '响应式适配'],
    github: 'https://github.com/maple-red/Html3Base'
  }
]

/** 获取首页展示的前 3 个项目 */
export function getFeaturedProjects() {
  return projects.slice(0, 3)
}
