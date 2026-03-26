import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import { createPinia } from 'pinia';
import { createDiscreteApi } from 'naive-ui';
import App from './App.vue';
import routes from './router';
import { setupMicroApps } from './micro-apps';

// 样式
import './styles/main.scss';

// 创建应用实例
const app = createApp(App);

// 创建路由
const router = createRouter({
  history: createWebHistory(),
  routes
});

// 创建状态管理
const pinia = createPinia();

// 创建Naive UI离散API
const { message, notification, dialog, loadingBar } = createDiscreteApi(
  ['message', 'notification', 'dialog', 'loadingBar']
);

// 挂载全局属性
app.config.globalProperties.$message = message;
app.config.globalProperties.$notification = notification;
app.config.globalProperties.$dialog = dialog;
app.config.globalProperties.$loadingBar = loadingBar;

// 使用插件
app.use(router);
app.use(pinia);

// 启动微前端应用
setupMicroApps();

// 挂载应用
app.mount('#app');
