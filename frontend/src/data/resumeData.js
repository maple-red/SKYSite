/**
 * 简历数据
 * 基于胡天林真实简历
 */

export const resumeData = {
  personal: {
    name: '胡天林',
    title: 'Java 全栈开发实习生',
    email: 'chif04571@gmail.com',
    phone: '15085957525',
    location: '贵阳',
    bio: '贵州大学软件工程专业本科在读。热爱编程，具备扎实的 Java 后端开发基础与 Vue 前端开发能力，熟悉微服务架构与容器化部署，对 Agent 开发有初步了解。'
  },

  education: [
    {
      school: '贵州大学（211 / 双一流）',
      major: '软件工程',
      degree: '工学本科',
      period: '2023.09 - 2027.07',
      status: '大三在读',
      description: 'GPA 排名前 30%，主修 Java Web、Docker、WEB-UI 设计等',
      achievements: [
        '"邻缘益康"项目获互联网+校三等奖',
        '蓝桥杯 C++ A 类赛道省三等奖'
      ]
    }
  ],

  projects: [
    {
      name: 'e舍宿连',
      role: '全栈开发',
      tech: ['Spring Boot', 'Vue3', 'MyBatis', 'JWT', 'Spring Security'],
      period: '2026.02 - 2026.05',
      description: '面向高校的双角色宿舍管理平台，支持住宿管理、报修处理、调/退宿申请等核心业务。',
      details: [
        'Spring Security + JWT 无状态认证',
        '路由守卫动态权限隔离',
        'Druid 连接池 + SQL 监控'
      ]
    },
    {
      name: '校园帮帮递',
      role: '全栈开发',
      tech: ['Spring Cloud', 'Vue3', 'Docker'],
      period: '学习实践中',
      description: '基于微服务架构的校园快递代取服务平台，涵盖服务注册发现、配置中心、网关路由。',
      details: [
        '微服务架构拆分与通信',
        '服务熔断与降级'
      ]
    },
    {
      name: '地震数据分析',
      role: '独立开发',
      tech: ['Python', 'Pandas', '爬虫'],
      period: '大二',
      description: '爬取地震台网数据并进行清洗分析与可视化展示。',
      details: [
        '爬取地震台网数据并清洗',
        '可视化展示分布与趋势'
      ]
    },
  ],

  skills: [
    { name: 'Java', level: 85 },
    { name: 'Spring Boot', level: 82 },
    { name: 'Vue3', level: 80 },
    { name: 'MySQL', level: 78 },
    { name: 'Docker', level: 72 },
    { name: 'Git / Linux', level: 78 }
  ],

  hobbies: [
    { name: '编程' },
    { name: '算法' },
    { name: '篮球' },
    { name: '摄影' }
  ]
}
