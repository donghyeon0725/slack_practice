// vue 를 로드한다.
import Vue from 'vue';
// 설치한 라우터를 로드한다.
import VueRouter from 'vue-router';
import store from '@/store/index';

// 뷰 어플리케이션에 라우터 플러그인을 추가한다.
Vue.use(VueRouter);

const router = new VueRouter({
  mode: 'history',
  routes: [
    {
      path: '/',
      redirect: 'login',
    },
    {
      path: '/login',
      component: () => import('@/views/LoginPage.vue'),
    },
    {
      path: '/main',
      component: () => import('@/views/MainPage.vue'),
      meta: {
        auth: true,
      },
    },
    {
      path: '/main/:teamId',
      component: () => import('@/views/MainPage.vue'),
      meta: {
        auth: true,
        socket: true,
        socket_target: 'team',
      },
      children: [
        {
          path: ':boardId',
          component: () => import('@/views/BoardPage.vue'),
        },
      ],
    },
    {
      // 회원가입 메일 요청 페이지
      path: '/signup',
      component: () => import('@/views/SignupPage.vue'),
    },
    {
      // 회원가입 페이지
      path: '/join/:email/:token',
      component: () => import('@/views/JoinPage.vue'),
    },
    {
      // 팀 초대 메일 수락 페이지
      path: '/invite/:email/:token',
      component: () => import('@/views/TeamJoinPage.vue'),
    },
    {
      path: '*',
      component: () => import('@/views/NotFountPage.vue'),
    },
  ],
}); // 새로운 라우터를 생성해서 export

// 라우터 가드
router.beforeEach(async (to, from, next) => {
  // 로그인 상태일 때는 다시 로그인 페이지레 들어갈 수 없음
  if (to.path == '/login' && store.state.email != '') {
    next('/main');
    return;
  }

  // 로그인이 필요한 페이지인데, 로그인 상태가 아니면 로그인 페이지로 보낸다.
  if (to.meta.auth && store.state.email == '') {
    console.log('인증이 필요합니다');
    // login 페이지로
    next('/login');
    // return 필수
    return;
  }

  // 이외 경우는 모두, 원래대로 진행한다.
  next();
});

export default router;
