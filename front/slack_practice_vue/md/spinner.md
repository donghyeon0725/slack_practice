📌 전체 흐름
-

1. data에 로딩 중인지 확인할 수 있는 값 추가
```javascript
  data() {
    return {
      isLoading: false, // true 로 변경해서 확인해보세요.
    };
  },
```
2. isLoading이 true이면 스피너 보여줌
```html
<!-- 당연히, 컴포넌트로 등록 해야함 -->
<LoadingSpinner v-if="isLoading"></LoadingSpinner>
```

3. await가 끝나면 isLoading을 false로 변경
```javascript
    async fetchData() {
      this.isLoading = true;
      const { data } = await fetchPosts();
      this.isLoading = false;
      this.postItems = data.posts;
    },
```



<br/>

📌 스피너 뷰 예제 코드
-
```html
<template>
  <div class="spinner-container">
    <div class="spinner" />
  </div>
</template>

<script>
export default {};
</script>

<style scoped>
.spinner-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 240px;
}
.spinner {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  border: 5px solid #e0e0e0;
  border-bottom: 5px solid #fe9616;
  animation: spin 1s linear infinite;
  position: relative;
}
@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
```
