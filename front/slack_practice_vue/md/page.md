📌 페이지 작성
-
* 컴포넌트 작성하기 (보통 비지니스 로직별로 분리해서 모듈을 만듭니다)
```html
<template>
  <header>
    <router-link to="/login">로그인</router-link
    ><!-- router-link를 통해서 다른 url로 보낼 수 있음 -->&nbsp;|&nbsp;
    <router-link to="/signup">회원가입</router-link
    ><!-- to라는 속성을 통해서 링크를 보냄 -->
  </header>
</template>

<script>
import Demo from '@/demo/basic/Demo';
// import Demo from '@/demo/basic/Demo'

export default {};
</script>

<style></style>
```

* 모듈을 불러올 페이지에서, 컴포넌트의 대상으로 추가
```html
<AppHeader></AppHeader><!-- 추가한 모듈 넣기 -->

<script >
    import AppHeader from '@/components/common/AppHeader.vue';
    
    export default {
      components: {
        AppHeader,
      },
    };
</script>
```


<br/>


📌 빠른 vue 페이지 작성
-
* vue snippets에서 제공하는 단축어인 "vim"을 입력하면 리스트로 viewport가 뜰텐데, 이것을 선택하여 폼을 빠르게 완성할 수 있다.



<br/>


📌 뷰 페이지 생성 원칙
-
* 비지니스 로직을 담는 모듈 : components 폴더 아래에, 컴포넌트 별로 작성을 한다.
* 뼈대(페이지)가 되는 컴포넌트 : views 폴더 아래에 작성 한다. 만든 components 파일 들을 불러와 사용한다.


<br/>


📌 vue 컴포넌트의 data 속성 단축어
-
* vda


<br/>


