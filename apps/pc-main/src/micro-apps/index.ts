import { registerMicroApps, start, initGlobalState } from 'qiankun';
import { useAuthStore } from '../stores/auth';

// 微前端应用配置
const microApps = [
  {
    name: 'pc-personal', // 个人用户应用
    entry: '//localhost:8081',
    container: '#subapp-viewport',
    activeRule: '/personal',
    props: {
      routerBase: '/personal',
      getGlobalState: () => {
        const authStore = useAuthStore();
        return {
          user: authStore.user,
          companyId: authStore.companyId,
          token: authStore.token
        };
      }
    }
  },
  {
    name: 'pc-business', // 企业用户应用
    entry: '//localhost:8082',
    container: '#subapp-viewport',
    activeRule: '/business',
    props: {
      routerBase: '/business',
      getGlobalState: () => {
        const authStore = useAuthStore();
        return {
          user: authStore.user,
          companyId: authStore.companyId,
          token: authStore.token
        };
      }
    }
  },
  {
    name: 'pc-admin', // 管理端应用
    entry: '//localhost:8083',
    container: '#subapp-viewport',
    activeRule: '/admin',
    props: {
      routerBase: '/admin',
      getGlobalState: () => {
        const authStore = useAuthStore();
        return {
          user: authStore.user,
          companyId: authStore.companyId,
          token: authStore.token
        };
      }
    }
  }
];

// 初始化全局状态
const initGlobalStates = () => {
  const authStore = useAuthStore();

  // 初始化全局状态
  const { onGlobalStateChange, setGlobalState } = initGlobalState({
    user: authStore.user,
    companyId: authStore.companyId,
    token: authStore.token
  });

  // 监听全局状态变化
  onGlobalStateChange((newState, prev) => {
    console.log('主应用监听到全局状态变化:', newState, prev);
    // 可以在这里同步状态到主应用的store
  });

  // 返回设置全局状态的方法
  return setGlobalState;
};

// 应用生命周期钩子
const microAppConfig = {
  beforeLoad: (app: any) => {
    console.log('before load %c%s', 'color: green;', app.name);
  },
  beforeMount: (app: any) => {
    console.log('before mount %c%s', 'color: green;', app.name);
  },
  afterMount: (app: any) => {
    console.log('after mount %c%s', 'color: green;', app.name);
  },
  beforeUnmount: (app: any) => {
    console.log('before unmount %c%s', 'color: green;', app.name);
  },
  afterUnmount: (app: any) => {
    console.log('after unmount %c%s', 'color: green;', app.name);
  }
};

// 设置微前端应用
export const setupMicroApps = () => {
  // 初始化全局状态
  const setGlobalState = initGlobalStates();

  // 注册微前端应用
  registerMicroApps(microApps, microAppConfig);

  // 启动qiankun
  start({
    sandbox: {
      strictStyleIsolation: true, // 严格样式隔离
      experimentalStyleIsolation: true
    },
    prefetch: false // 关闭预加载，提升首屏加载速度
  });

  // 导出设置全局状态的方法，供其他地方使用
  return setGlobalState;
};
