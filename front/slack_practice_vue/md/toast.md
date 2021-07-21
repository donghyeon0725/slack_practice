📌 토스트 별도 사용
-
* 부트스트랩 토스트 기능에, 토스트가 open 되자마자, 투명도가 0이 되어서 사라지는 것 처럼 보이는 현상이 발견 되었다.
* css 등의 속성을 별도로 추가하면, 해당 버그를 고칠수는 있겠지만, 그러면 해당 css를 만드시 포함 (전역 영역에 포함 하면 되겠지만, 의존성이 생기는 것이 싫어서)하여야 하기 때문에 완전하지 않다고 생각
* 따라서, 다른 라이브러리를 사용해서 별도로 모듈화 해두기로 함.
* 이슈 : <https://github.com/bootstrap-vue/bootstrap-vue/issues/4498>, <https://github.com/bootstrap-vue/bootstrap-vue/issues/3934>
* 위 링크를 참고했으나, 버그는 고쳐지지 않음


<br/>


📌 vue-toast-notification 공식 문서
-
* <https://www.npmjs.com/package/vue-toast-notification> 


<br/>


📌 설치
-
```javascript
import VueToast from 'vue-toast-notification';
// Import one of the available themes
// import 'vue-toast-notification/dist/theme-default.css';
import 'vue-toast-notification/dist/theme-sugar.css';

Vue.use(VueToast);
```


<br/> 

📌 사용
-
```javascript
this.$toast.open({
    message: '메세지',
    position: 'top-right',
    duration: 3000,
});
```

