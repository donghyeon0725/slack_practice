<template>
  <div style="padding: 5px">
    <b-button
      v-b-modal="modal_id"
      block
      variant="outline-dark"
      style="width: 100%"
      >Team Create</b-button
    >

    <b-modal :id="modal_id" title="Team" button-size="sm" @ok="createTeam">
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
        <b-button size="sm" variant="primary" @click="ok()">create</b-button>
        <b-button size="sm" variant="danger" @click="cancel()">
          cancel
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<script>
import { createTeam } from '@/api/team';

export default {
  name: 'TeamForm',
  data() {
    return {
      modal_id: 'TeamCreateModal',
      form: {
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
    async createTeam() {
      let msg = '';

      try {
        let { data } = await createTeam(this.form);
        msg = '팀이 생성 되었습니다.';

        this.$defualtToast(msg);
        await this.$store.dispatch('refreshTeamsAndEmptyOther');
        await this.$router.push('/main/' + data.id);
      } catch (e) {
        if (
          Object.prototype.hasOwnProperty.call(e, 'response') &&
          e.response.status == 409
        ) {
          msg = '이미 팀을 생성하셨습니다.';
        } else {
          msg = '에러가 발생했습니다.';
        }

        this.$defualtToast(msg, { type: 'error' });
      }
    },
  },
  mounted() {
    this.$root.$on('bv::modal::show', (evt, mId) => {
      if (mId == this.modal_id) {
        this.onReset();
      }
    });
  },
};
</script>
