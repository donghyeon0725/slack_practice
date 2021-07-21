<template>
  <div class="c-container">
    <div class="side">
      <Side></Side>
    </div>
    <div class="center">
      <router-view :key="$route.fullPath"></router-view>
    </div>
  </div>
</template>

<script>
import Side from '@/views/MainSidePage';
import { getSocketUrl } from '@/api/socket';
import socket from '@/socket/real-time-client';

export default {
  name: 'MainPage',
  components: { Side },
  data() {
    return {
      loading: false, // true로 변경해서 확인해보세요.
    };
  },
  async created() {
    await getSocketUrl().then(s => {
      let url = s.data;
      console.log(url);
      socket.init(url, this.$store.state.token);
    });
  },
  updated() {},
};
</script>

<style>
.c-container {
  height: 100vh;
  padding: 0;
  display: flex;
  margin: 0;
  padding: 0;
  width: 100%;
  overflow-x: hidden;
  box-sizing: border-box;
}

.side {
  background: #f7f6f3;
  flex-basis: 500px;
  position: relative;
  /* fixed child 요소를 포함하기 위해서는 아래와 같은 속성이 필요합니다. */
  transform: translate3d(0, 0, 0);
}

.center {
  width: 100%;
  overflow: scroll;
}
</style>
