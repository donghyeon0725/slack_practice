📌 마크다운 지원하기
-
* marked js 라이브러리를 사용해서 markdown 을 지원 합니다.
<br/>

📌 과정
-
1. 글자 입력
2. html로 변환 된 텍스트를 만들어주는 computed 속성 작성 (라이브러리 사용)
3. 화면에 보여줄 때에는 html으로 보여주어야 합니다. (DB에 저장될 때는 날것 그대로의 상태로 저장되어 있음)


<br/>

📌 시작하기
-
* marked 라이브러리 설치
```text
npm install marked
```

* markdown화 된 텍스트를 보여주는 vue 컴포넌트
```html
<template>
  <div id="app">
    <div class="container">
      <!--The main App-->
      <span v-html="previewText"></span>
    </div>
  </div>
</template>

<script>
import marked from 'marked';

export default {
  name: 'Markdown',
  data() {
    return {
      md_text: '### 헤더', // 이 곳에 원하는 텍스트를 넣습니다.
    };
  },
  computed: {
    previewText() {
      marked.setOptions({
        renderer: new marked.Renderer(),
        gfm: true,
        tables: true,
        breaks: true,
        pedantic: false,
        sanitize: true,
        smartLists: true,
        smartypants: false,
      });
      return marked(this.md_text);
    },
  },
};
</script>

<style scoped></style>
```


