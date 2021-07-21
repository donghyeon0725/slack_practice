<template>
  <div>
    <b-modal :id="id" title="Board" button-size="sm" @ok="createBoard">
      <template #modal-header="{}">
        <h5>Board</h5>
      </template>

      <b-form @reset="onReset">
        <b-form-group
          id="input-group-1"
          label="Board Title:"
          label-for="input-1"
        >
          <b-form-input
            id="input-1"
            v-model="form.title"
            type="text"
            placeholder="Enter Board Title"
            autocomplete="off"
            required
          ></b-form-input>
        </b-form-group>

        <b-form-group
          id="input-group-2"
          label="Board Description:"
          label-for="input-2"
          class="mb-2"
        >
          <b-form-input
            id="input-2"
            v-model="form.content"
            placeholder="Board Description"
            autocomplete="off"
            required
          ></b-form-input>
        </b-form-group>
      </b-form>

      <template #modal-footer="{ ok, cancel }">
        <!-- Emulate built in modal footer ok and cancel button actions -->
        <b-button size="sm" variant="primary" @click="ok()">create</b-button>
        <b-button size="sm" variant="danger" @click="cancel()">
          cancle
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<script>
import { createBoard } from '@/api/board';

export default {
  name: 'BoardForm',
  props: {
    id: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      form: {
        title: '',
        content: '',
        teamId: '',
      },
    };
  },
  methods: {
    onSubmit(event) {
      event.preventDefault();
      alert(JSON.stringify(this.form));
    },
    onReset() {
      this.form.title = '';
      this.form.content = '';
    },
    async createBoard() {
      let msg = '';
      try {
        msg = '보드가 생성 되었습니다.';
        let { data } = await createBoard(this.form);

        this.$defualtToast(msg);
        let teamId = this.$route.params.teamId;

        await this.$store.dispatch('refreshOnlyBoards', teamId);
        await this.$router.push('/main/' + teamId + '/' + data.id);
      } catch (e) {
        console.log(e);
        if (e.response.status == 409) {
          msg = '이미 보드를 생성하셨습니다.';
        }
        this.$defualtToast(msg, { type: 'warning' });
      }
    },
  },
  mounted() {
    this.$root.$on('bv::modal::show', (evt, mId) => {
      if (mId == this.id) this.onReset();

      this.form.teamId = this.$route.params.teamId;
    });
  },
};
</script>
