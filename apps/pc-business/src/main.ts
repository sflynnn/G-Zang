import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import { createPinia } from 'pinia';
import { createDiscreteApi } from 'naive-ui';
import App from './App.vue';
import routes from './router';
import i18n from './i18n';
import './styles/main.css';
import './styles/main.scss';

let app: any;
let router: any;
let history: any;

function createAppInstance(props: any = {}) {
  const base = props.routerBase || '/';
  history = createWebHistory(base);
  router = createRouter({ history, routes });
  const pinia = createPinia();

  app = createApp(App);

  const { message, notification, dialog, loadingBar } = createDiscreteApi(
    ['message', 'notification', 'dialog', 'loadingBar']
  );
  app.config.globalProperties.$message = message;
  app.config.globalProperties.$notification = notification;
  app.config.globalProperties.$dialog = dialog;
  app.config.globalProperties.$loadingBar = loadingBar;

  app.use(router);
  app.use(pinia);
  app.use(i18n);

  return app;
}

if (!(window as any).__POWERED_BY_QIANKUN__) {
  const app = createAppInstance();
  app.mount('#app');
}

export async function bootstrap() { console.log('pc-business bootstrap'); }
export async function mount(props: any) {
  const app = createAppInstance(props);
  app.mount(props.container || '#app');
}
export async function unmount() {
  if (app) { app.unmount(); app = null; }
  if (history) { history.destroy(); history = null; }
  if (router) { router = null; }
}
