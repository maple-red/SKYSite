import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Splash',
    component: () => import('@/views/SplashView.vue')
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/HomeView.vue')
  },
  {
    path: '/about',
    name: 'About',
    component: () => import('@/views/AboutView.vue')
  },
  {
    path: '/projects',
    name: 'Projects',
    component: () => import('@/views/ProjectsView.vue')
  },
  {
    path: '/resume',
    name: 'Resume',
    component: () => import('@/views/ResumeView.vue')
  },
  {
    path: '/message',
    name: 'Message',
    component: () => import('@/views/MessageBoard.vue')
  },
  {
    path: '/moments',
    name: 'Moments',
    component: () => import('@/views/MomentsView.vue')
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/home'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // 滚动归零由 App.vue 的 @before-leave 处理
    // 这里只负责浏览器的前进/后退恢复位置
    if (savedPosition) {
      return savedPosition
    }
  }
})

export default router
