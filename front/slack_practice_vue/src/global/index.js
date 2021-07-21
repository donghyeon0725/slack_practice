/* 글로벌로 사용할 함수와 컴포넌트 등록 */
import Modal from '@/util/modal';
import Toast from '@/util/toast';
import { isNotEmpty, isEmpty } from '@/util/validation';

// 훅이 필요할 때 사용합니다.
const swichers = {
  created: true,
  beforeMount: true,
  mounted: true,
  destroyed: true,
};

let plugin = {};
// options은 인스턴스 생성시 넘겨주는 options 값 입니다. => 별도 설정이 필요할 경우 사용합니다.
plugin.install = function (Vue, options) {
  // options 와 swichers를 병합
  Object.assign(swichers, options);
  // 인스턴스 메소드 => 실행 컨텍스트가 Modal 이 되어도 상관이 없다면 사용
  // Vue.prototype.$inst = Modal;
  // 인스턴스 메소드 => 실행컨텍스트가 현재 컴포넌트(이 메소드를 실행하는 객체)를 가리켜야 한다면, 아래와 같이 사용해야 한다.
  Vue.prototype.$confirmModal = Modal.confirmModal;
  Vue.prototype.$msgModal = Modal.msgModal;
  Vue.prototype.$defualtToast = Toast.defualt;
  Vue.prototype.$isNotEmpty = isNotEmpty;
  Vue.prototype.$isEmpty = isEmpty;

  // 전역 메소드
  Vue.myGlobalMethod = function () {};
};

export default plugin;
