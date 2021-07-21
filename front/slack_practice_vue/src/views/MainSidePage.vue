<template>
  <div class="c-side-container">
    <Team></Team>
  </div>
</template>

<script>
import Team from '@/components/main/Team';

export default {
  name: 'MainSidePage',
  components: { Team },
};
</script>

<style>
.c-side-container {
  width: 100%;
  max-width: inherit;
  position: fixed;
}
</style>
