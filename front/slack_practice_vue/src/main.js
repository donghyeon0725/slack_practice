import Vue from 'vue';
import App from './App.vue';
import router from '@/routes/index'; // 라우터 생성. 이때 굳이 .js는 사용하지 않아도 된다.
import store from '@/store/index'; // vuex import
import '@/filter/index'; // filter
import globalPlugin from '@/global/index'; // global

// 웹소켓
import eventBus from '@/socket/event-bus';
import realTimeClient from '@/socket/real-time-client';
Vue.prototype.$bus = eventBus;
Vue.prototype.$rt = realTimeClient;

// 부트 스트랩
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
Vue.use(BootstrapVue);
Vue.use(IconsPlugin);

// bootstrap 토스트 버그로 다른 라이브러리 사용
import VueToast from 'vue-toast-notification';
import 'vue-toast-notification/dist/theme-sugar.css';
Vue.use(VueToast);

// global 함수
Vue.use(globalPlugin);

Vue.config.productionTip = false;

new Vue({
  render: h => h(App),
  router, // 라우터 장착
  store,
}).$mount('#app');
