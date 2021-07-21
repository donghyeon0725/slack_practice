<template>
  <div class="accordion" role="tablist">
    <b-card
      no-body
      class="mb-1"
      v-for="(team, team_idx) in teams"
      :key="team_idx"
      :active="isActiveTeam(team.id)"
    >
      <b-card-header
        header-tag="header"
        class="p-1"
        role="tab"
        @click="changeActive(team.id)"
      >
        <b-button
          block
          v-b-toggle="'accordion' + team_idx"
          variant="info"
          style="width: 100%; height: 100%"
          >{{ team.name }}</b-button
        >
      </b-card-header>

      <b-collapse
        :id="'accordion' + team_idx"
        :visible="isActiveTeam(team.id)"
        accordion="my-accordion"
        role="tabpanel"
        style="background: #f7f6f3"
      >
        <b-card-body>
          <b-card-text>
            <div class="d-flex w-100 justify-content-between">
              <h5 class="mb-1">
                {{ team.name }}
                <!-- 팀 수정 버튼 -->
                <TeamEditForm v-if="team.state == 'CREATOR'"></TeamEditForm>
                <!-- 팀 삭제 버튼 -->
                <b-link block @click="deleteTeam" v-if="team.state == 'CREATOR'"
                  ><b-icon icon="x"></b-icon
                ></b-link>
              </h5>
              <small>{{ team.date | dayAgo }} days ago</small>
            </div>
          </b-card-text>
          <b-card-text class="pb-2" style="border-bottom: 1px solid gray">
            {{ team.description }}
          </b-card-text>

          <!-- 보드 생성 버튼 && 보드 폼 -->
          <b-card-text>
            <b-button
              v-b-modal="'board_modal-' + team_idx"
              block
              variant="outline-dark"
              style="width: 100%"
              >Create Board</b-button
            >
            <BoardForm :id="'board_modal-' + team_idx"></BoardForm>
          </b-card-text>

          <!-- 보드 -->
          <b-card-text>
            <Board
              v-if="isActiveTeam(team.id)"
              :isActived="isActiveTeam(team.id)"
            ></Board>
          </b-card-text>
        </b-card-body>
      </b-collapse>
    </b-card>

    <!-- 팀 생성 버튼 + TeamForm 모달 -->
    <TeamForm></TeamForm>
  </div>
</template>

<script>
import { deleteTeam, getTeams } from '@/api/team';
import Board from '@/components/main/side/Board';
import TeamForm from '@/components/main/side/TeamForm';
import BoardForm from '@/components/main/side/BoardForm';
import TeamEditForm from '@/components/main/side/TeamEditForm';

export default {
  name: 'Team',
  components: {
    Board,
    TeamForm,
    BoardForm,
    TeamEditForm,
  },
  methods: {
    async changeActive(id) {
      if (this.$route.params.teamId == id) return;
      await this.$router.push('/main/' + id);
    },
    async deleteTeam() {
      let modal = {
        title: '삭제',
        message: '팀을 삭제 하시 겠습니까?',
      };

      let confirm = await this.$confirmModal(modal.title, modal.message);
      if (confirm) {
        let msg = '';
        try {
          let selectedTeamId = this.$route.params.teamId;
          await deleteTeam(selectedTeamId);
          msg = '삭제 성공했습니다';
          this.$defualtToast(msg);
        } catch (e) {
          msg = '에러가 발생했습니다.';
          console.log(e);
          this.$defualtToast(msg, { type: 'error' });
        }

        await this.$store.dispatch('refreshTeamsAndEmptyOther');
        await this.$router.push('/main');
      }
    },
  },
  computed: {
    isActiveTeam() {
      return id => {
        let teamId = this.$route.params.teamId;
        if (this.$isEmpty(teamId)) return false;

        return id == this.$route.params.teamId;
      };
    },
    teams: {
      get() {
        return this.$store.state.page.teams;
      },
      set(list) {
        this.$store.commit('setTeams', list);
      },
    },
  },
  async created() {
    try {
      let { data } = await getTeams();
      this.$store.commit('setTeams', data);
    } catch (e) {
      console.log(e);
    }

    // 값 확인
    let teams = this.$store.state.page.teams;
    let id = this.$route.params.teamId;

    /*
      1. 팀이 없는 경우
      2. 팀은 있는데, 팀 입력값이 잘못된 경우 => pageNotFound
      3. 팀이 있는데, 선택을 안한 경우
      4. 팀은 있도 있고, 팀 입력값도 있는 경우 => 해당 페이지로
      * */
    if (teams.length <= 0) {
      // 팀이 없는 경우
      console.log('팀이 없습니다');
      return;
    } else {
      if (this.$isEmpty(id)) {
        console.log('팀이 있는데, 선택을 안한 경우');
        return;
      }
      teams = teams.filter(s => s.id == id);
      // 팀이 있고, 팀을 못 찾은 경우
      if (teams.length <= 0) {
        console.log('팀이 있고, 팀을 못 찾은 경우');
        // await this.$router.push('/pageNotFound');
      }
      // 팀이 있고, 팀을 찾은 경우
      else {
        console.log('팀이 있고, 팀을 찾은 경우');
      }
    }
  },
};
</script>
<style>
.close {
  border: none;
  background: none;
  float: right;
  font-size: 1.5rem;
  font-weight: 700;
  line-height: 1;
  color: #000;
  text-shadow: 0 1px 0 #fff;
  opacity: 0.5;
}
.btn-block.collapsed {
  background-color: #c4e2ec;
  border-color: #c4e2ec;
  font-weight: 900;
}
.btn-block.not-collapsed {
  background-color: #c4e2ec;
  border-color: #c4e2ec;
  font-weight: 900;
}
h5 {
  font-weight: 600;
}
</style>
