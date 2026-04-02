import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import { createPinia } from 'pinia';
import { createDiscreteApi } from 'naive-ui';
import App from './App.vue';
import routes from './router';
import i18n from './i18n';

// 样式
import './styles/main.css';

// 创建应用实例（延迟创建）
let app: any;
let router: any;
let history: any;

// 创建应用函数
function createAppInstance(props: any = {}) {
  // 创建路由（支持基路径）
  const base = props.routerBase || '/';
  history = createWebHistory(base);
  router = createRouter({
    history,
    routes
  });

  // 创建状态管理
  const pinia = createPinia();

  // 创建应用
  app = createApp(App);

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
  app.use(i18n);

  // 设置全局状态（从主应用传递）
  if (props.getGlobalState) {
    const globalState = props.getGlobalState();
    // 可以在这里设置全局状态
    console.log('接收到主应用全局状态:', globalState);
  }

  return app;
}

// 独立运行时直接挂载
if (!(window as any).__POWERED_BY_QIANKUN__) {
  const app = createAppInstance();
  app.mount('#app');
}

// 微前端生命周期函数
export async function bootstrap() {
  console.log('pc-personal bootstrap');
}

export async function mount(props: any) {
  console.log('pc-personal mount', props);

  // 创建并挂载应用
  const app = createAppInstance(props);
  app.mount(props.container || '#app');
}

export async function unmount() {
  console.log('pc-personal unmount');

  // 卸载应用
  if (app) {
    app.unmount();
    app = null;
  }

  // 清理路由
  if (history) {
    history.destroy();
    history = null;
  }

  if (router) {
    router = null;
  }
}
