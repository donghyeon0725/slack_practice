import Vue from 'vue';
import Vuex from 'vuex';
import actions from '@/store/actions';
import state from '@/store/state';
import mutations from '@/store/mutations';
/**
 * vuex를 사용합니다.
 * */
Vue.use(Vuex);

export default new Vuex.Store({
  state,
  mutations,
  actions,
});
