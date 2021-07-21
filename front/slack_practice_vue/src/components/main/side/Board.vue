<template>
  <b-nav v-if="boards.length > 0">
    <b-nav-item-dropdown
      :id="'dropdown' + team.id"
      :text="selectedBoard.title"
      toggle-class="nav-link-custom"
      right
      style="width: inherit"
    >
      <b-dropdown-item
        style="width: 100%"
        :key="'dropdown' + board_idx"
        v-for="(board, board_idx) in boards"
      >
        <b-nav-item
          :to="{
            path: '/main/' + teamId + '/' + board.id,
          }"
          >{{ board.title }}</b-nav-item
        ></b-dropdown-item
      >
      <!--<b-dropdown-divider></b-dropdown-divider>-->
    </b-nav-item-dropdown>
  </b-nav>
</template>

<script>
import { getBoards } from '@/api/board';

export default {
  name: 'Board',
  data() {
    return {
      // 이 컴포넌트의 teamId가 변경 될 때
      teamId: '',
    };
  },
  props: {
    isActived: {
      type: Boolean,
      required: true,
    },
  },
  methods: {
    async getBoards() {
      // boards 초기화
      if (this.isActived) {
        this.teamId = this.$route.params.teamId;

        try {
          let teamId = this.$route.params.teamId;
          let { data } = await getBoards(teamId);
          await this.$store.dispatch('setBoards', data);
        } catch (e) {
          console.log(e);
        }
      }
    },
    onRealTimeHandler({ type, data }) {
      if (this.$isEmpty(data)) {
        this.$store.dispatch(type, this);
      } else {
        console.log(type, data);
        this.$store.dispatch(type, data);
      }
    },
    async subscribe() {
      let teamId = this.$route.params.teamId;
      if (this.$isNotEmpty(teamId)) {
        let teams = this.$store.state.page.teams;
        teams = teams.filter(s => s.id == teamId);
        if (teams.length > 0) {
          console.log('subscribe team ' + teamId);
          this.$rt.subscribe(
            this.$rt.channel.team(teamId),
            this.onRealTimeHandler,
          );
        }
      }
    },
  },
  watch: {
    // prop을 watch 하려면 deep 모드를 켜야한다.
    isActived: {
      immediate: true,
      deep: true,
      handler() {
        // state의 값을 변화 시키기
        this.getBoards();
        this.subscribe();
      },
    },
  },
  computed: {
    team: {
      get() {
        if (this.isActived)
          return this.$store.state.page.teams.filter(
            s => s.id == this.$route.params.teamId,
          )[0];

        return {};
      },
      set(obj) {
        this.$store.commit('setSelectedTeam', obj);
      },
    },
    boards: {
      get() {
        return this.$store.state.page.boards;
      },
      set(obj) {
        this.$store.commit('setBoards', obj);
      },
    },
    selectedBoard: {
      get() {
        if (this.isActived) {
          let boardId = this.$route.params.boardId;
          if (this.$isNotEmpty(boardId)) {
            let selectedBoard = this.$store.state.page.boards.filter(
              s => s.id == boardId,
            )[0];
            if (Object.keys(selectedBoard).length != 0) return selectedBoard;
          }
        }

        return { title: '보드를 선택하세요.' };
      },
      set(obj) {
        this.$store.commit('setSelectedBoard', obj);
      },
    },
  },
};
</script>

<style scoped></style>
