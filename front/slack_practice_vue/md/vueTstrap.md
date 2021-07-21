📌 뷰 부트스트랩 시작하기
-
> <https://bootstrap-vue.org/docs> 아래 링크로 들어가면 자세한 내용을 확인할 수 있다.
* 설치하기
```text
npm install vue bootstrap bootstrap-vue
```

* main.js 에 사용하겠다고 명시하기
```javascript
import Vue from 'vue'
import { BootstrapVue, IconsPlugin } from 'bootstrap-vue'

// Import Bootstrap an BootstrapVue CSS files (order is important)
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'

// Make BootstrapVue available throughout your project
Vue.use(BootstrapVue)
// Optionally install the BootstrapVue icon components plugin
Vue.use(IconsPlugin)
```

* 컴포넌트 가져와 사용하면 끝
