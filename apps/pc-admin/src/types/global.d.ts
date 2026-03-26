// 全局类型声明

import type { MessageApi, NotificationApi, DialogApi, LoadingBarApi } from 'naive-ui';

declare global {
  interface Window {
    $message: MessageApi;
    $notification: NotificationApi;
    $dialog: DialogApi;
    $loadingBar: LoadingBarApi;
  }
}
