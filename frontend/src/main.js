import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import './style.css'

// 阻止浏览器自动恢复滚动位置，统一由 Vue 管理
history.scrollRestoration = 'manual'

const app = createApp(App)
app.use(ElementPlus)
app.use(router)
app.mount('#app')
