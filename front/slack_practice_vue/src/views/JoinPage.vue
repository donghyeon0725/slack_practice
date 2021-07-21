<template>
  <div
    style="
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
    "
  >
    <JoinForm></JoinForm>
  </div>
</template>

<script>
import JoinForm from '@/components/join/JoinForm.vue';

export default {
  name: 'JoinPage',
  components: {
    JoinForm,
  },
};
</script>

<style>
#app {
  margin: 20px;
}
</style>
