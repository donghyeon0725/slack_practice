📌 필터 생성하기
-
* 필터를 생성하는 방법은 지역으로 생성하는 방법 & 전역으로 생성하는 방법 2가지가 있다.


<br/>


📌 지역 필터 생성하기
-
* 컴포넌트로 등록만 하고 사용하면 된다.
```javascript
filters: {
  capitalize: function (value) {
    if (!value) return ''
    value = value.toString()
    return value.charAt(0).toUpperCase() + value.slice(1)
  }
}
```

* 호출
```html
<!-- 중괄호 보간법 -->
{{ message | capitalize }}

<!-- v-bind 표현 -->
<div v-bind:id="rawId | formatId"></div>
```

<br/>

📌 전역 필터
-
* 아래는 전역 필터로 등록하는 방법은 아래와 같이 한다. Vue 인스턴스 생성 이전에 등록해야 한다.
```javascript
import Vue from 'vue';

Vue.filter('capitalize', function (value) {
  if (!value) return ''
  value = value.toString()
  return value.charAt(0).toUpperCase() + value.slice(1)
})

new Vue({
...
})
```
* 전역 필터로 등록한 메소드는 위와 동일한 방식으로 호출하면 된다.
* 또는, Filter 로 등록하는 로직을 별도 파일로 분리하고 main.js 에서 import 하여 사용하는 방법도 있다.

```javascript
import Vue from 'vue';
import { formatDate } from '@/util/date';

Vue.filter('formatDate', formatDate);
```
* 위 파일을, main.js 에서 import 하면 된다.

