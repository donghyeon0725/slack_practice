<template>
  <span>
    <b-link v-b-modal="modal_id" block><b-icon icon="pencil"></b-icon></b-link>

    <b-modal :id="modal_id" title="Team" button-size="sm" @ok="modifyTeam">
      <template #modal-header="{}">
        <h5>Team</h5>
      </template>

      <b-form @reset="onReset">
        <b-form-group
          id="input-group-1"
          label="Team Title:"
          label-for="input-1"
        >
          <b-form-input
            id="input-1"
            v-model="form.name"
            type="text"
            placeholder="Enter Team Title"
            autocomplete="off"
            required
          ></b-form-input>
        </b-form-group>

        <b-form-group
          id="input-group-2"
          label="Team Description:"
          label-for="input-2"
          class="mb-2"
        >
          <b-form-input
            id="input-2"
            v-model="form.description"
            placeholder="Team Description"
            autocomplete="off"
            required
          ></b-form-input>
        </b-form-group>
      </b-form>

      <template #modal-footer="{ ok, cancel }">
        <b-button size="sm" variant="primary" @click="ok()">update</b-button>
        <b-button size="sm" variant="danger" @click="cancel()">
          cancel
        </b-button>
      </template>
    </b-modal>
  </span>
</template>

<script>
import { editTeam } from '@/api/team';

export default {
  name: 'TeamEditForm',
  data() {
    return {
      modal_id: 'team-edit-form',
      form: {
        id: '',
        name: '',
        description: '',
      },
    };
  },
  methods: {
    onSubmit(event) {
      event.preventDefault();
      alert(JSON.stringify(this.form));
    },
    onReset() {
      this.form.name = '';
      this.form.description = '';
    },
    async modifyTeam() {
      let msg = '';
      try {
        await editTeam(this.form);
        msg = '수정 되었습니다.';
        this.$defualtToast(msg);
      } catch (e) {
        if (e.response.status == 409) {
          msg = '수정 도중 에러가 났습니다.';
        }
        this.$defualtToast(msg, { type: 'error' });
      }

      await this.$store.dispatch('refreshOnlyTeams');
      // await this.$router.push(this.$router.currentRoute);
    },
  },
  mounted() {
    this.$root.$on('bv::modal::show', async (evt, mId) => {
      if (mId == this.modal_id) {
        this.onReset();

        let teamId = this.$route.params.teamId;
        let teams = this.$store.state.page.teams;
        let team = teams.filter(s => s.id == teamId)[0];

        this.form.id = team.id;
        this.form.name = team.name;
        this.form.description = team.description;
      }
    });
  },
};
</script>
