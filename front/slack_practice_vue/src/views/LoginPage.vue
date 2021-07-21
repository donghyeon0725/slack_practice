<template>
  <div
    style="
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
    "
  >
    <LoginForm></LoginForm>
  </div>
</template>

<script>
import LoginForm from '@/components/login/LoginForm.vue';

export default {
  name: 'LoginPage',
  components: {
    LoginForm,
  },
};
</script>

<style>
#app {
  margin: 20px;
}
</style>
