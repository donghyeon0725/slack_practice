<template>
  <div
    style="
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
    "
  >
    <SignupForm></SignupForm>
  </div>
</template>

<script>
import SignupForm from '@/components/signup/SignupForm.vue';

export default {
  name: 'app',
  components: {
    SignupForm,
  },
};
</script>

<style>
#app {
  margin: 20px;
}
</style>
