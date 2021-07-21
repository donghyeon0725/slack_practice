<template>
  <div>
    <div class="f-container">
      <!-- 드로그 앤 드롭 -->
      <draggable
        tag="ul"
        v-model="cards"
        :options="{ group: 'people' }"
        @start="drag = true"
        @end="drag = false"
        :component-data="getComponentData()"
        :draggable="hasAuthFormThisBoard ? '.card-content' : ''"
      >
        <transition-group type="transition" :name="'flip-list'">
          <li
            :class="
              card.isSelected ? 'fit card-content selected' : 'fit card-content'
            "
            :key="card.position"
            v-for="card in cards"
            @click="showCard(card.id)"
            style="cursor: pointer"
          >
            <b-card
              :title="card.title"
              v-if="card.attachments == null"
              :img-src="'https://picsum.photos/600/300/?image=25'"
              img-alt="Image"
              img-top
              tag="article"
              style="max-width: 20rem"
              class="mb-2"
            >
              <b-card-text>
                {{ card.content | cutTextAtStart(10) }}
              </b-card-text>

              <div style="float: right">
                <b-button
                  v-if="
                    isCardCreator(card.state) ||
                    isBoardCreator(card.state) ||
                    isTeamCreator(card.state)
                  "
                  @click="stopPropagation"
                  :to="{
                    path: '/main/' + teamId + '/' + boardId,
                    query: { cardId: card.id, detail: false },
                  }"
                  variant="outline-primary"
                  class="card-modify-btn"
                  >Modify</b-button
                >
                &nbsp;
                <b-button
                  v-if="
                    isCardCreator(card.state) ||
                    isBoardCreator(card.state) ||
                    isTeamCreator(card.state)
                  "
                  class="card-delete-btn"
                  @click="stopPropagation, deleteCard(card.id)"
                  variant="outline-danger"
                  >Delete</b-button
                >
              </div>
            </b-card>

            <!-- 널이 아닐 때 -->
            <b-card
              :title="card.title"
              v-else-if="card.attachments.length > 0"
              :img-src="
                ref +
                'getImage/' +
                card.attachments[0].path
                  .replaceAll('\\', '@')
                  .replaceAll('/', '@') +
                '@' +
                card.attachments[0].systemFilename
                  .replaceAll('\\', '@')
                  .replaceAll('/', '@')
              "
              img-alt="Image"
              img-top
              tag="article"
              style="max-width: 20rem"
              class="mb-2"
            >
              <b-card-text>
                {{ card.content | cutTextAtStart(10) }}
              </b-card-text>

              <div style="float: right">
                <b-button
                  v-if="
                    isCardCreator(card.state) ||
                    isBoardCreator(card.state) ||
                    isTeamCreator(card.state)
                  "
                  @click="stopPropagation"
                  :to="{
                    path: '/main/' + teamId + '/' + boardId,
                    query: { cardId: card.id, detail: false },
                  }"
                  variant="outline-primary"
                  class="card-modify-btn"
                  >Modify</b-button
                >
                &nbsp;
                <b-button
                  v-if="
                    isCardCreator(card.state) ||
                    isBoardCreator(card.state) ||
                    isTeamCreator(card.state)
                  "
                  class="card-delete-btn"
                  @click="stopPropagation, deleteCard(card.id)"
                  variant="outline-danger"
                  >Delete</b-button
                >
              </div>
            </b-card>
          </li>
        </transition-group>
      </draggable>
    </div>
  </div>
</template>

<script>
import { deleteCard, updateCardPosition } from '@/api/card';
import Draggable from 'vuedraggable';

export default {
  name: 'Card',
  components: {
    Draggable,
  },
  data() {
    return {
      teamId: this.$route.params.teamId,
      boardId: this.$route.params.boardId,
      ref: process.env.VUE_APP_API_URL,
      activeNames: '',
    };
  },
  methods: {
    handleChange() {
      console.log('변화 감지');
    },
    inputChanged(value) {
      this.activeNames = value;
    },
    getComponentData() {
      return {
        on: {
          change: this.handleChange,
          input: this.inputChanged,
        },
        attrs: {
          wrap: true,
        },
        props: {
          value: this.activeNames,
        },
      };
    },
    // 이벤트 버블링 방지
    stopPropagation(event) {
      event.stopPropagation();
    },
    showCard(id) {
      let teamId = this.$route.params.teamId;
      let boardId = this.$route.params.boardId;

      this.$router.push({
        path: '/main/' + teamId + '/' + boardId,
        query: { cardId: id, detail: true },
      });
    },
    async deleteCard(cardId) {
      let modal = {
        title: '삭제',
        message: '카드를 삭제 하시 겠습니까?',
      };

      // 확인 모달 호출
      let confirm = await this.$confirmModal(modal.title, modal.message);
      if (confirm) {
        try {
          let { data } = await deleteCard(cardId);
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
      }
    },
    isCardCreator(auth) {
      return auth == 'CARD_CREATOR';
    },
    isBoardCreator(auth) {
      return auth == 'BOARD_CREATOR';
    },
    isTeamCreator(auth) {
      return auth == 'CREATOR';
    },
  },
  computed: {
    hasAuthFormThisBoard() {
      let board = this.$store.state.page.boards.filter(
        s => s.id == this.$route.params.boardId,
      )[0];

      if (this.$isEmpty(board)) return false;
      return board.state == 'CREATOR' || board.state == 'BOARD_CREATOR';
    },
    // 기본적으로 store에서 불러온 card를 가지고 컴포넌트를 랜더링 하나, cards의 데이터를 변화시키는 시점은 이 컴포넌트가 활성화 되는 시점임(watch)
    cards: {
      get() {
        let filterWord = this.$store.state.page.filterWord;

        // 필터에 단어가 있는 경우
        if (this.$isNotEmpty(filterWord)) {
          // 드래깅 기능 꺼야함 => 예측 어려운 버그를 막기 위함
          let cardList = document.querySelectorAll('.card-content');
          for (let c of cardList) {
            c.classList.remove('card-content');
          }
          return this.$store.state.page.cards.filter(s =>
            new RegExp(filterWord, 'gi').test(s.title),
          );
        } else {
          let cardList = document.querySelectorAll('.fit');
          for (let c of cardList) {
            c.classList.add('card-content');
          }
        }

        return this.$store.state.page.cards;
      },
      async set(cards) {
        // 카드의 이동이 일어났을 때, 상태 데이터를 넣어 줍니다.
        // 데이터의 순서 뿐 아니라, position 자체를 변경했을 경우, animation 효과가 아닌, 재 랜더링을 하기 때문에, position을 번경하지 않았습니다.

        let copy = [];
        let position = 0;
        for (let card of cards) {
          let json = {};
          json.id = card.id;
          json.position = ++position;
          copy.push(json);
        }
        // capy 본을 서버에 저장합니다.
        try {
          let { data } = await updateCardPosition({ cards: copy });

          // position 데이터를 변경해줍니다. 이때, 데이터의 인덱스 순서는 변경이 일어나지 않도록 합니다.
          // for (let d of data)
          //   for (let card of this.$store.state.page.cards)
          //     if (card.id == d.id) card.position = d.position;

          this.$defualtToast('위치 변경');
          console.log(data);
        } catch (e) {
          console.log(e);
          this.$defualtToast('실패', { type: 'error' });
        }

        this.$store.commit('setCards', cards);
      },
    },
  },
  updated() {
    let deleteBtn = document.querySelectorAll('button.card-delete-btn');
    let modifyBtn = document.querySelectorAll('a.card-modify-btn');

    if (deleteBtn.length > 0)
      for (let btn of deleteBtn)
        btn.addEventListener('click', this.stopPropagation);

    if (modifyBtn.length > 0)
      for (let btn of modifyBtn)
        btn.addEventListener('click', this.stopPropagation);
  },
};
</script>

<style>
.f-container {
  display: flex;
  flex-flow: row wrap;
}
.fit {
  width: fit-content;
  display: inline-block;
  margin-right: 2vw;
  margin-bottom: 2vh;
}

.flip-list-move {
  transition: transform 0.5s;
}

.no-move {
  transition: transform 0s;
}

.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}
</style>
