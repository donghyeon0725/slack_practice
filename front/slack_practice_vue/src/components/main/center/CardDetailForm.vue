<template>
  <div class="card-detail">
    <b-modal
      :id="modal_id"
      v-model="isCardDetailActive"
      no-fade
      size="xl"
      hide-footer
      hide-header
      centered
    >
      <div class="custom-cover">
        <img
          v-b-tooltip.hover
          title="사진 크게 보기"
          style="cursor: pointer"
          @click="picture = !picture"
          :src="
            card.attachments.length > 0
              ? ref +
                'getImage/' +
                card.attachments[0].path
                  .replaceAll('\\', '@')
                  .replaceAll('/', '@') +
                '@' +
                card.attachments[0].systemFilename
                  .replaceAll('\\', '@')
                  .replaceAll('/', '@')
              : 'https://picsum.photos/600/300/?image=25'
          "
          alt="image"
        />

        <b-modal
          id="picture"
          v-model="picture"
          no-fade
          size="lg"
          hide-footer
          hide-header
          centered
        >
          <img
            :src="
              card.attachments.length > 0
                ? ref +
                  'getImage/' +
                  card.attachments[0].path
                    .replaceAll('\\', '@')
                    .replaceAll('/', '@') +
                  '@' +
                  card.attachments[0].systemFilename
                    .replaceAll('\\', '@')
                    .replaceAll('/', '@')
                : 'https://picsum.photos/600/300/?image=25'
            "
            alt="image"
          />
        </b-modal>
      </div>

      <div class="section">
        <div class="content">
          <div class="title">{{ card.title }}</div>
          <div class="description">
            <Markdown
              :mdText="card.content"
              :visible="true"
              :option="{
                display: 'block',
                width: '100%',
                left: '505px',
                top: '50px',
                height: '100%',
                position: 'static',
                opacity: 1,
                backgroundColor: 'rgba(244,252,255,0.5)',
                container: {
                  padding: '20px 30px',
                },
              }"
            ></Markdown>
          </div>
        </div>
        <div class="line"></div>
        <div class="replies">
          <b-form-group
            id="fieldset-1"
            description="Let us know your opinion."
            label="Enter Reply"
            label-for="input-1"
            valid-feedback="Thank you!"
          >
            <b-form-textarea
              id="reply-textarea"
              placeholder="Enter something..."
              v-model="form.content"
              rows="3"
              max-rows="6"
            ></b-form-textarea>
          </b-form-group>
          <br />
          <div
            :key="index"
            v-for="(reply, index) in card.replies"
            class="reply"
          >
            <div class="i-line">
              <div class="user-icon-side">
                <b-icon icon="github" class="user-icon"></b-icon>
              </div>
              <div class="user-name">{{ reply.teamMember.user.name }}</div>
              <div class="reply-date">{{ reply.date | formatDate }}</div>
              <div class="reply-delete" @click="deleteReply(reply.id)">
                <b-icon icon="x-circle-fill" class="delete-icon"></b-icon>
              </div>
            </div>
            <div class="reply-content">{{ reply.content }}</div>
          </div>
        </div>
      </div>
    </b-modal>
  </div>
</template>

<script>
import { createReply, deleteReply } from '@/api/card';
import Markdown from '@/components/common/Markdown';

export default {
  name: 'CardDetailForm',
  components: {
    Markdown,
  },
  data() {
    return {
      modal_id: 'cardDetailForm',
      init: false,
      picture: false,
      ref: process.env.VUE_APP_API_URL,
      form: {
        id: '',
        cardId: '',
        content: '',
      },
    };
  },
  computed: {
    isCardDetailActive: {
      get: function () {
        // 아이디가 있어야 하고, 취소 버튼을 누르지 않은 상태여야 한다. 또한 카드 목록에 있어야 한다.
        let cards = this.$store.state.page.cards;
        let cardId = this.$route.query.cardId;
        let isDetailMode = this.$route.query.detail == 'true';

        if (cards.length <= 0) return false;
        if (this.$isEmpty(cardId)) return false;

        if (cards.filter(s => s.id == cardId).length > 0)
          if (isDetailMode) return true;

        return false;
      },
      set: function (isModalShown) {
        if (!isModalShown) {
          this.$router.push(this.$router.currentRoute.path);
        }
      },
    },
    card() {
      let cardId = this.$route.query.cardId;

      return (
        this.$store.state.page.cards.filter(s => s.id == cardId)[0] || {
          attachments: [],
          replies: [],
        }
      );
    },
  },
  methods: {
    async deleteReply(replyId) {
      try {
        let { data } = await deleteReply(replyId);
        console.log(data);

        this.$defualtToast('삭제 성공');

        await this.$store.dispatch(
          'refreshOnlyCards',
          this.$route.params.boardId,
        );
      } catch (e) {
        console.log(e);
      }
    },
    async createReplyWithEnter(e) {
      if (e.key == 'Enter' && !e.shiftKey) {
        // 기본 엔터 동작 막기
        e.preventDefault();
        let cardId = this.$route.query.cardId;
        console.log(cardId);

        this.form.cardId = cardId;
        console.log(this.form.cardId);

        try {
          let { data } = await createReply(this.form);
          console.log(data);

          this.onReset();

          await this.$store.dispatch(
            'refreshOnlyCards',
            this.$route.params.boardId,
          );
        } catch (e) {
          console.log(e);
        }

        // 모달 닫기
        // this.isCardDetailActive = !this.isCardDetailActive;
      }

      /*await this.$store.dispatch(
        'refreshOnlyCards',
        this.$route.params.boardId,
      );*/
    },
    onReset() {
      this.form.id = '';
      this.form.cardId = '';
      this.form.content = '';
    },
  },
  async updated() {
    if (!this.init) {
      // textarea 의 enter를 전송 처리로 바꾸고, crtl + enter을 new line 처리로 바꿉니다.
      let textarea = document.getElementById('reply-textarea');

      if (this.$isNotEmpty(textarea))
        textarea.addEventListener('keydown', this.createReplyWithEnter);

      this.init = !this.init;
    }
  },
};
</script>

<style>
#cardDetailForm .modal-body {
  height: 90vh;
  overflow-y: auto;
  scroll-bar: 10px;
}

.modal-body::-webkit-scrollbar {
  width: 10px;
}

.modal-body::-webkit-scrollbar-thumb {
  background-color: #afafaf;
}
.modal-body::-webkit-scrollbar-track {
  background-color: #ced4da;
}

.replies label {
  padding-bottom: 5px;
}
</style>

<style scoped>
.custom-cover {
  width: -webkit-fill-available;
  position: absolute;
  height: 20vh;
  left: 0;
  top: 0;
  overflow: hidden;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
}
.custom-cover img {
  width: 100%;
}
.content {
  padding-top: 22vh;
  padding-bottom: 5vh;
  max-width: 100%;
  width: 100%;
}
.content .title {
  font-size: 60px;
  padding-bottom: 20px;
}
.content .description {
  font-size: 24px;
}
.content .icon {
  background-color: #e4e5e6;
  display: inline-block;
  width: 30px;
  height: 30px;
  border: 1px solid #e4e5e6;
  border-radius: 100%;
  position: relative;
  top: 3px;
}
div.section {
  padding-left: calc(126px + env(safe-area-inset-left));
  padding-right: calc(126px + env(safe-area-inset-right));
}
div.line {
  border: 1px solid #e4e5e6;
  position: relative;
  width: 100%;
  height: 2px;
  left: 0;
  background-color: #e4e5e6;
  border-radius: 100%;
  margin-bottom: 4vh;
}

.reply .user-icon {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 1px solid black;
  border-radius: 100%;
  position: relative;
  top: 3px;
}

.i-line .reply-delete {
  display: inline-block;
  float: right;
  margin-right: 20px;
  cursor: pointer;
  opacity: 0;
}
.reply:hover .reply-delete {
  opacity: 1;
  transition-delay: 0.2s;
  transition-duration: 1s;
  transition-timing-function: ease;
}
.reply .delete-icon {
  display: inline-block;
  width: 30px;
  height: 30px;
  border: 1px solid black;
  border-radius: 100%;
  position: relative;
  top: 5px;
}

.reply .user-icon-side {
  display: inline-block;
  padding-right: 10px;
}
.reply .user-name {
  display: inline-block;
  font-size: 16px;
  font-weight: 600;
  padding-right: 10px;
}

.reply .reply-date {
  display: inline-block;
  font-size: 12px;
  font-weight: 300;
}

.reply .reply-content {
  display: block;
  padding: 10px 30px 10px 30px;
  white-space: pre-wrap;
}
</style>
