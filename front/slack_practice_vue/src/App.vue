<template>
  <div>
    <Header></Header>
    <router-view></router-view>
  </div>
</template>

<script>
import Header from '@/components/common/Header';

export default {
  name: 'app',
  components: {
    Header,
  },
  watch: {
    $route(to, from) {
      if (to.path != from.path) {
        if (from.meta.socket) {
          let toTeamId = to.params.teamId;
          let fromTeamId = from.params.teamId;
          if (this.$isNotEmpty(fromTeamId) && fromTeamId != toTeamId) {
            console.log('unsubscribe team ' + fromTeamId);
            this.$rt.unsubscribe(
              this.$rt.channel[from.meta.socket_target](fromTeamId),
              this.onRealTimeHandler,
            );
          }
        }
      }
      /*if (to.path != from.path) {
        /!* router path가 변경될 때마다 호출 *!/
        let toTeamId = to.params.teamId;
        if (to.meta.socket) {
          if (this.$isNotEmpty(toTeamId)) {
            let teams = this.$store.state.page.teams;
            teams = teams.filter(s => s.id == toTeamId);
            if (teams.length > 0) {
              console.log('subscribe team ' + toTeamId);
              this.$rt.subscribe(
                this.$rt.channel[to.meta.socket_target](toTeamId),
                this.onRealTimeHandler,
              );
            }
          }
        }
        if (from.meta.socket) {
          let fromTeamId = from.params.teamId;
          console.log(fromTeamId, toTeamId);
          if (this.$isNotEmpty(fromTeamId) && fromTeamId != toTeamId) {
            console.log('unsubscribe team ' + fromTeamId);
            this.$rt.unsubscribe(
              this.$rt.channel[from.meta.socket_target](fromTeamId),
              this.onRealTimeHandler,
            );
          }
        }
      }*/
    },
  },
};
</script>

<style>
body {
  overflow: hidden;
}
</style>
