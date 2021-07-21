📌 전역 메소드 등록
-
* 지속적으로 사용하는 컴포넌트의 경우, 매번 import 후, component에 등록해서 불러오는 코드를 사용하는 행위가 매우 비효율적이므로 플러그인 형태로 만들어 전역으로 등록해서 사용할 필요성이 생겼음


<br/>

📌 플러그인이란?
-
* 다들 알고 있겠지만, 쉽게 말하자면 프로그램의 확장 기능 같은 것이다.
* 자바스크립트에서 플러그인이란, 정해진 메소드나 객체를 "약속된 형태로 불러와" 사용하는 것을 의미한다.
* 따라서 vue js 에서도 플러그인 등록을 위해서는 정해진 방법이 있습니다.


<br/>


📌 공식문서
-
들어가기에 앞서서 공식문서를 확인하기 바랍니다. 공식 문서에 플러그인 등록을 위한 방법을 알려주고 있습니다.
* <https://kr.vuejs.org/v2/guide/plugins.html#%ED%94%8C%EB%9F%AC%EA%B7%B8%EC%9D%B8-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0>


<br/>

📌 요약
-
* 플러그인으로 등록할 메소드 생성
* 해당 메소드로 플러그인을 생성합니다. 이때 파일의 이름은 index.js 가 될 겁니다.
* 만든 index.js 를 main.js 에서 import (전역으로 등록하기)
* 사용하기



<br/>

📌 플러그인으로 등록할 메소드 생성하기
-
* 필자의 경우 모달을 플러그인으로 등록해서 사용할 예정입니다.
* 확인이나 alert 용도의 메소드를 호출할 때마다 import 하는 것은 매우 비효율적이기 때문입니다.
<details>
    <summary>펼치기</summary>


```javascript
let modal = {
  confirmModal: function (title, message, options) {
    let config = {
      title: title,
      size: 'sm',
      buttonSize: 'sm',
      okVariant: 'primary',
      okTitle: '확인',
      cancelTitle: '취소',
      footerClass: 'p-2',
      hideHeaderClose: false,
      centered: true,
    };
    // 별도 사용할 설정이 있는 경우
    Object.assign(config, options);

    let result = this.$bvModal
      .msgBoxConfirm(message, config)
      .then(value => {
        // 다음 작업이 필요한 경우 사용
        if (value) return true;
        else return false;
      })
      .catch(err => {
        // An error occurred
        console.log(err);
        return false;
      });
    console.log(result);
    return result;
  },
};

export default modal;
```
</details>


<br/>


📌 해당 메소드로 플러그인을 생성하기.
-
* 만든 메소드를 플러그인으로 생성하는 부분입니다.
* 이때 주의 할 점은, 모달에서 사용할 메소드도 플러그인에서 호출 해야할 메소드가 포함되어 있습니다.
* 즉, this 키워드 (실행컨텍스트)가 vue Component를 바라볼 수 있도록 하려면 아래와 같이, 메소드를 담은 객체를 넣을 것이 아니라, 메소드 자체를 넣어줘야 합니다. 자세한 내용은 아래 소스를 참고합니다.

<details>
    <summary>펼쳐보기</summary>

```javascript
/* 글로벌로 사용할 함수와 컴포넌트 등록 */
import Modal from '@/util/modal';

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

  // 전역 메소드
  Vue.myGlobalMethod = function () {};
};

export default plugin;

```

</details>


<br/>

📌 만든 index.js 를 main.js 에서 import 하기 (전역으로 등록하기)
-
* 만든 메소드를 main.js 에서 import 합니다.  
<details>
    <summary>펼쳐보기</summary>

```javascript
import globalPlugin from '@/global/index'; // global 파일 import

// 위 메소드에서 사용할 부트스트랩 플러그인
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
Vue.use(BootstrapVue);
Vue.use(IconsPlugin);

// global 플러그인으로써 등록
Vue.use(globalPlugin);

... 중략
```

</details>


<br/>

📌 사용하기
-
* 아래와 같은 형태로 사용합니다.

```javascript
let modal = {
  title: '삭제',
  message: '보드를 삭제 하시 겠습니까?',
};

// 확인 모달 호출
let confirm = await this.$confirmModal(modal.title, modal.message);
if (confirm) {
console.log('확인 누름');
}
```


