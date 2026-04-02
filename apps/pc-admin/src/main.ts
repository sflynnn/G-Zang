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

const app = createApp(App);

// 注册全局指令
app.directive('loading', vLoading);
app.directive('permission', vPermission);
app.directive('role', vRole);

// 状态管理
const pinia = createPinia();

app.use(pinia);
app.use(router);
app.use(naive);
app.use(i18n);

// 挂载应用
app.mount('#app');

// 创建离散API（在应用挂载后）
const { message, notification, dialog, loadingBar, modal } = createDiscreteApi(
  ['message', 'notification', 'dialog', 'loadingBar', 'modal']
);

// 挂载到window对象上
(window as any).$message = message;
(window as any).$notification = notification;
(window as any).$dialog = dialog;
(window as any).$loadingBar = loadingBar;
(window as any).$modal = modal;
