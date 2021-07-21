📌 라우터 가드
-
* 페이지의 이동이 발생하기 전에 앞서, 특정 처리를 해주고 싶을 때 사용한다.
* 이름에서 알 수 있듯이 vue-router가 제공하는 네비게이션 가드는 주로 리디렉션하거나 취소하여 네비게이션을 보호하는 데 사용됩니다.


<br/>

📌 공식 문서
-
* <https://router.vuejs.org/kr/guide/advanced/navigation-guards.html>


<br/>


📌 사용하기
-
* src/routes 폴더 아래의 index.js 페이지를 수정한다.
```javascript
const router = new VueRouter({
    ...
})

// 아래 코드를 추가해주면 된다.
router.beforeEach((to, from, next) => {
  // 로그인이 필요한 페이지인데, 로그인 상태가 아니면 로그인 페이지로 보낸다.
  if (to.meta.auth && !store.getters.isLogin) {
    console.log('인증이 필요합니다');
    // login 페이지로
    next('/login');
    // return 필수
    return;
  }
  // 이외 경우는 모두, 원래대로 진행한다.
  next();
});
```

* 위 코드를 보면 to.meta.auth 라는 값이 if 문 안에 들어가 있는 것이 보이는데, 해당 값은 페이지에 권한이 필요한지 여부를 부여하기 위함이다.
* 아래와 같은 라우터 객체를 라우터에 끼우면, 해당 페이지는 권한이 필요한 것이 된다.
```javascript
{
  path: '/main',
  component: () => import('@/views/MainPage.vue'),
  meta: { auth: true },
},
```


