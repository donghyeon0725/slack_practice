<template><div>asd</div></template>

<script>
import { acceptInvite } from '@/api/team';
export default {
  name: 'TeamJoinPage',
  async created() {
    // 기존에 로그인 되어있던 계정 로그 아웃 수행하기
    await this.$store.dispatch('logout');

    let email = this.$route.params.email;
    let token = this.$route.params.token;
    try {
      let { data } = await acceptInvite(email, token);
      console.log(data);
    } catch (e) {
      console.log(e);
    }
    await this.$router.push('/login');
  },
};
</script>

<style scoped></style>
