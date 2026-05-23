import { createApp } from 'vue';
import { createPinia } from 'pinia';
import { createDiscreteApi } from 'naive-ui';
import naive from 'naive-ui';

import App from './App.vue';
import router from './router';
import i18n from './i18n';
import { vLoading } from './directives/loading';
import { vPermission, vRole } from './directives/permission';
import './styles/main.scss';

let app: any;
let pinia: any;

// Qiankun 全局状态代理
let globalStateProxy: any = null;

function createAppInstance(props: any = {}) {
  const base = props.routerBase || '/';

  // 如果是从主应用传入的状态，获取全局状态
  if (props.getGlobalState && typeof props.getGlobalState === 'function') {
    globalStateProxy = props.getGlobalState();
  }

  app = createApp(App);

  // 注册全局指令
  app.directive('loading', vLoading);
  app.directive('permission', vPermission);
  app.directive('role', vRole);

  // 状态管理
  pinia = createPinia();

  app.use(pinia);
  app.use(router);
  app.use(naive);
  app.use(i18n);

  // 创建离散API
  const { message, notification, dialog, loadingBar, modal } = createDiscreteApi(
    ['message', 'notification', 'dialog', 'loadingBar', 'modal']
  );

  // 挂载到window对象上
  app.config.globalProperties.$message = message;
  app.config.globalProperties.$notification = notification;
  app.config.globalProperties.$dialog = dialog;
  app.config.globalProperties.$loadingBar = loadingBar;
  app.config.globalProperties.$modal = modal;

  // 挂载微前端全局状态代理
  app.config.globalProperties.$microState = globalStateProxy;

  return app;
}

// 独立运行模式
if (!(window as any).__POWERED_BY_QIANKUN__) {
  const instance = createAppInstance();
  instance.mount('#app');
}

// Qiankun 生命周期函数
export async function bootstrap() {
  console.log('[pc-admin] Bootstrap finished');
}

export async function mount(props: any) {
  console.log('[pc-admin] Mount with props:', props);
  const instance = createAppInstance(props);
  instance.mount(props.container || '#app');

  // 监听主应用全局状态变化
  if (props.onGlobalStateChange) {
    props.onGlobalStateChange((state: any) => {
      console.log('[pc-admin] Global state changed:', state);
      // 同步到本地store或状态管理
      if (globalStateProxy) {
        // 可以在这里更新本地状态
      }
    });
  }
}

export async function unmount() {
  console.log('[pc-admin] Unmount');
  if (app) {
    app.unmount();
    app = null;
  }
  if (pinia) {
    pinia = null;
  }
  globalStateProxy = null;
}
