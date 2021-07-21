<template>
  <div>
    <b-modal :id="id" title="Card" button-size="sm" @ok="createCard">
      <template #modal-header="{}">
        <h5>Card</h5>
      </template>

      <b-form @reset="onReset">
        <b-form-group
          id="input-group-1"
          label="Card Title:"
          label-for="input-1"
        >
          <b-form-input
            id="input-1"
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
            id="CardCreateModalContent"
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
        <b-button size="sm" variant="primary" @click="ok()">create</b-button>
        <b-button size="sm" variant="danger" @click="cancel()">
          cancel
        </b-button>
      </template>
    </b-modal>
  </div>
</template>

<script>
import { createCard } from '@/api/card';
import Markdown from '@/components/common/Markdown';

export default {
  name: 'CardForm',
  components: {
    Markdown,
  },
  props: {
    id: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      markdown: false,
      form: {
        title: '',
        content: '',
        file: null,
        boardId: '',
      },
    };
  },
  computed: {
    isMarkdownShow() {
      return this.markdown;
    },
  },
  methods: {
    // 마크다운을 보여주고 끕니다.
    lookMarkdown() {
      this.markdown = true;
    },
    blindMarkdown() {
      this.markdown = false;
    },
    onReset() {
      this.form.title = '';
      this.form.content = '';
      this.form.file = null;
      this.form.boardId = '';
    },
    async createCard() {
      let modal = {
        msg: '',
        title: '카드 생성결과',
      };
      try {
        this.form.boardId = this.$route.params.boardId;
        console.log(this.form.boardId);

        let { data } = await createCard(this.form);
        console.log(data);

        modal.msg = '카드가 생성 되었습니다.';
        this.$defualtToast(modal.msg);

        let boardId = this.$route.params.boardId;

        await this.$store.dispatch('refreshOnlyCards', boardId);
      } catch (e) {
        console.log(e);
        if (e.response.status == 409) {
          modal.msg = '에러';
        }
        this.$defualtToast(modal.msg, { type: 'error' });
      }
    },
  },
  mounted() {
    this.$root.$on('bv::modal::show', (evt, mId) => {
      if (mId == this.id) this.onReset();
    });
  },
};
</script>

<style>
#fileupload {
  position: relative;
}
#fileupload label {
  display: inline-block;
  padding: 0.5em 0.75em;
  color: #999;
  font-size: inherit;
  line-height: normal;
  vertical-align: middle;
  background-color: #fdfdfd;
  cursor: pointer;
  border: 1px solid #ebebeb;
  border-bottom-color: #e2e2e2;
  border-radius: 0.25em;
  width: 100%;
}
#fileupload input[type='file'] {
  /* 파일 필드 숨기기 */
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  border: 0;
}
.custom-file-label:after {
  bottom: 0;
  z-index: 3;
  display: block;
  height: calc(1.6em + 0.75rem);
  content: 'select file';
  background-color: #e9ecef;
  border-left: inherit;
  border-radius: 0 0.25rem 0.25rem 0;
}
.custom-file-label:after {
  position: absolute;
  top: 1px;
  right: 0;
  padding: 0.375rem 0.75rem;
  line-height: 1.5;
  color: black;
}
</style>
