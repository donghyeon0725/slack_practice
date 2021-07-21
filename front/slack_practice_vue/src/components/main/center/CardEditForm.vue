<template>
  <b-modal :id="modal_id" v-model="isCardModifyActive" @ok="modifyCard">
    <template #modal-header="{}">
      <h5>Card</h5>
    </template>

    <b-form>
      <b-form-group id="input-group-1" label="Card Title:" label-for="input-1">
        <b-form-input
          id="CardEditModalInput"
          v-model="form.title"
          type="text"
          placeholder="Enter Card Title"
          autocomplete="off"
          required
        ></b-form-input>
      </b-form-group>

      <b-form-group
        id="input-group-2"
        label="Card Description:"
        label-for="input-2"
        class="mb-2"
      >
        <!-- 마크다운 -->
        <b-form-textarea
          id="CardEditModalContent"
          v-model="form.content"
          placeholder="Card Description"
          rows="10"
          max-rows="10"
          @focus="lookMarkdown"
          @blur="blindMarkdown"
          required
        ></b-form-textarea>
        <Markdown
          :mdText="form.content"
          :visible="isMarkdownShow"
          :option="{
            width: '500px',
            height: '400px',
            left: '505px',
            top: '50px',
            container: {
              padding: '30px 30px',
            },
          }"
        ></Markdown>
      </b-form-group>

      <!-- file input -->
      <label>File Upload:</label>
      <b-form-group id="fileupload" class="dragdrop">
        <!-- Styled -->
        <b-form-file
          id="fileInput"
          v-model="form.file"
          :state="Boolean(form.file)"
          placeholder="Choose a file or drop it here..."
          drop-placeholder="Drop file here..."
        ></b-form-file>
        <div class="mt-3">
          Selected file: {{ form.file ? form.file.name : '' }}
        </div>
      </b-form-group>
    </b-form>

    <template #modal-footer="{ ok, cancel }">
      <!-- Emulate built in modal footer ok and cancel button actions -->
      <b-button size="sm" variant="primary" @click="ok()">update</b-button>
      <b-button size="sm" variant="danger" @click="cancel()"> cancel </b-button>
    </template>
  </b-modal>
</template>

<script>
import { modifyCard } from '@/api/card';
import Markdown from '@/components/common/Markdown';

export default {
  name: 'CardEditForm',
  components: {
    Markdown,
  },
  data() {
    return {
      modal_id: 'cardEditForm',
      form: {
        id: '',
        title: '',
        content: '',
        file: null,
      },
      markdown: false,
    };
  },
  computed: {
    isCardModifyActive: {
      get: function () {
        // 아이디가 있어야 하고, 취소 버튼을 누르지 않은 상태여야 한다. 또한 카드 목록에 있어야 한다.
        let cards = this.$store.state.page.cards;
        let cardId = this.$route.query.cardId;
        let isDetailMode = this.$route.query.detail == 'true';

        if (cards.length <= 0) return false;
        if (this.$isEmpty(cardId)) return false;

        if (cards.filter(s => s.id == cardId).length > 0)
          if (!isDetailMode) return true;

        return false;
      },
      set: function (isModalShown) {
        if (!isModalShown) {
          this.$router.push(this.$router.currentRoute.path);
        }
      },
    },
    isMarkdownShow() {
      return this.markdown;
    },
  },
  methods: {
    // 마크다운을 보여줍니다.
    lookMarkdown() {
      this.markdown = true;
    },
    blindMarkdown() {
      this.markdown = false;
    },
    async modifyCard() {
      if (Array.isArray(this.form.file)) {
        this.form.file = this.form.file[0];
      } else {
        if (this.form.file.size == 0) this.form.file = null;
      }
      console.log(this.form.file);

      try {
        let { data } = await modifyCard(this.form);
        console.log(data);
        this.$defualtToast('성공');
      } catch (e) {
        console.log(e);
        this.$defualtToast('에러', { type: 'error' });
      }

      await this.$store.dispatch(
        'refreshOnlyCards',
        this.$route.params.boardId,
      );
    },
    onReset() {
      this.form.id = '';
      this.form.title = '';
      this.form.content = '';
      this.form.file = null;
    },
  },
  mounted() {
    this.$root.$on('bv::modal::show', (evt, mId) => {
      if (mId == this.modal_id) {
        this.onReset();

        let cardId = this.$route.query.cardId;

        let selectedCard =
          this.$store.state.page.cards.filter(s => s.id == cardId)[0] || {};
        this.form.id = selectedCard.id;
        this.form.title = selectedCard.title;
        this.form.content = selectedCard.content;
        let file = selectedCard.attachments[0] || {};
        this.form.file = new File([''], file.filename);
      }
    });
  },
};
</script>

<style scoped></style>
