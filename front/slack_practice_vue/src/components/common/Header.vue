<template>
  <header
    style="background: linear-gradient(45deg, #e7e6e3, #cbbde2)"
    class="p-2"
  >
    <div v-if="!isLogin">
      <b-nav style="justify-content: flex-end">
        <b-nav-item :to="{ path: '/login' }">로그인</b-nav-item>
        <b-nav-item :to="{ path: '/signup' }">회원가입</b-nav-item>
      </b-nav>
    </div>
    <div v-if="isLogin">
      <b-nav style="justify-content: flex-end">
        <b-nav-item href="javascript:;" @click="logout">로그아웃</b-nav-item>
      </b-nav>
    </div>
  </header>
</template>

<script>
export default {
  name: 'Header',
  methods: {
    logout() {
      this.$store.dispatch('logout');
      this.$router.push('/');
    },
  },
  computed: {
    isLogin() {
      return this.$store.state.email != '';
    },
  },
};
</script>

<style scoped></style>
