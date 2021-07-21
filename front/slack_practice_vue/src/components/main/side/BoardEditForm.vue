<template>
  <div>
    <b-modal :id="id" title="Board" button-size="sm" @ok="modifyBoard">
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
        <b-button size="sm" variant="primary" @click="ok()">update</b-button>
        <b-button size="sm" variant="danger" @click="cancel()">
          cancel
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<script>
import { modifyBoard } from '@/api/board';

export default {
  name: 'BoardEditForm',
  data() {
    return {
      form: {
        title: '',
        content: '',
        id: this.$route.params.boardId,
      },
    };
  },
  props: {
    id: {
      type: String,
      required: true,
    },
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
    async modifyBoard() {
      let msg = '';

      try {
        msg = '보드가 수정 되었습니다.';
        await modifyBoard(this.form);
        this.$defualtToast(msg);
      } catch (e) {
        msg = '에러 발생';
        this.$defualtToast(msg, { type: 'error' });
      }

      await this.$store.dispatch(
        'refreshOnlyBoards',
        this.$route.params.teamId,
      );
    },
  },
  mounted() {
    this.$root.$on('bv::modal::show', async (evt, mId) => {
      if (mId == this.id) {
        let boardId = this.$route.params.boardId;
        let board =
          this.$store.state.page.boards.filter(s => s.id == boardId)[0] || [];

        this.form.title = board.title;
        this.form.content = board.content;
      }
    });
  },
};
</script>
