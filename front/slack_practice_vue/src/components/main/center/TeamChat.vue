<template>
  <div class="chat-module" id="chat-module">
    <b-button
      variant="outline-secondary"
      title="Align center"
      id="chat-btn"
      class="chat-btn"
      @click="toggleAct"
    >
      <b-icon icon="chat-fill"></b-icon>
    </b-button>
    <div class="chat-container" id="chat-container">
      <!-- 해더 -->
      <div class="header stop-dragging" id="chat-container-header">
        <div class="section">
          <!-- 다른 사람의 채팅 -->
          <div :key="chat.id" v-for="chat in chats">
            <div class="chat-others chat" v-if="!isMyMail(chat.email)">
              <div class="row-left">
                <div class="profile">
                  <b-icon icon="github" class="user-icon"></b-icon>
                </div>
              </div>
              <div class="row-right">
                <div class="name">{{ chat.email | cutTextAtStart(4) }}</div>
                <div class="chat-desc">
                  <b-icon
                    icon="exclamation"
                    class="user-icon"
                    v-if="isDeleted(chat)"
                  ></b-icon>
                  {{ chat.description }}
                </div>
              </div>
            </div>

            <!-- 나의 채팅 -->
            <div class="chat-mine chat" v-if="isMyMail(chat.email)">
              <div class="row-left"></div>
              <div class="row-right">
                <div class="chat-desc">
                  <ToolBox :chat="chat" @delete="deleteChat"></ToolBox>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 푸터 --->
      <div class="footer">
        <div class="top">
          <div class="textarea">
            <template>
              <div>
                <b-form-textarea
                  id="chat-textarea"
                  v-model="form.description"
                  placeholder="Enter something..."
                  rows="3"
                  max-rows="4"
                ></b-form-textarea>
              </div>
            </template>
          </div>
          <div class="submit">
            <b-button variant="outline-primary" @click="createChat"
              >전송</b-button
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getTeamChat, createTeamChat, deleteTeamChat } from '@/api/team';
import ToolBox from '@/components/common/ToolBox';
//deleteTeamChat,
export default {
  name: 'TeamChat',
  components: {
    ToolBox,
  },
  data() {
    return {
      form: {
        description: '',
        teamId: '',
      },
      page: 0,
      size: 10,
    };
  },
  computed: {
    chats() {
      return this.$store.state.page.teamChats;
    },
  },
  methods: {
    isDeleted(chat) {
      return chat.state == 'DELETED';
    },
    async deleteChat(chat) {
      console.log(chat);
      try {
        await deleteTeamChat(chat.id);
      } catch (e) {
        console.log(e);
      }
    },
    async createWithEnter(e) {
      if (e.key == 'Enter' && !e.shiftKey) {
        // 기본 엔터 동작 막기
        e.preventDefault();
        await this.createChat();
      }
    },
    isMyMail(email) {
      return email == this.$store.state.email;
    },
    async createChat() {
      if (this.form.description.trim().length <= 0) return;
      this.form.teamId = this.$route.params.teamId;

      try {
        await createTeamChat(this.form);
        this.page += 1;
      } catch (e) {
        console.log(e);
      }
      this.form.description = '';
    },
    preventRightClick(event) {
      event.preventDefault();
    },
    toggleAct() {
      document.getElementById('chat-container').classList.toggle('act');
      document.getElementById('chat-btn').classList.toggle('act');

      let chatBox = document.getElementById('chat-module');

      if (this.$isNotEmpty(chatBox))
        chatBox.addEventListener('contextmenu', this.preventRightClick);

      let textarea = document.getElementById('chat-textarea');

      if (this.$isNotEmpty(textarea))
        textarea.addEventListener('keydown', this.createWithEnter);
    },
  },
  async updated() {
    let scrollSection = document.getElementById('chat-container-header');
    scrollSection.scrollTop = scrollSection.scrollHeight;
  },
  async mounted() {
    try {
      let { data } = await getTeamChat(
        this.page,
        this.size,
        this.$route.params.teamId,
      );
      console.log(data);
      this.$store.commit('setChats', data);
    } catch (e) {
      console.log(e);
    }
  },
};
</script>

<style scoped>
.chat-container {
  z-index: 5;
  transition: all 0.3s;
  visibility: hidden;
  position: fixed;
  height: 0;
  right: 3vw;
  bottom: 5vh;
  border-radius: 40px;
  display: flex;
  flex-direction: column;
  border: 1px solid #e7e6e3;
}
.chat-container.act {
  visibility: visible;
  height: 700px;
}

.header {
  width: 500px;
  height: 550px;
  background: linear-gradient(45deg, #e7e6e3, #cbbde2);
  border-top-right-radius: 40px;
  border-top-left-radius: 40px;
  overflow: hidden;
  flex-shrink: 0;
}

.footer {
  height: 150px;
  background-color: white;
  border-radius: 40px;
}

.footer .top {
  display: flex;
}
.header {
  overflow-y: auto;
}

.header::-webkit-scrollbar {
  display: none;
}

.section {
  display: flex;
  flex-direction: column;
  width: 450px;
  margin: auto;
}
.chat {
  width: 100%;
  margin-bottom: 10px;
  display: flex;
  flex-direction: row;
}
.chat-others {
  justify-content: flex-start;
}
.chat-mine {
  justify-content: flex-end;
}

.chat-desc {
  padding: 5px;
  background-color: #42b983;
  border-radius: 5px;
  font-weight: 500;
  max-width: 210px;
}
.profile {
  padding: 10px;
}
.user-icon {
  font-size: 30px;
}
.name {
  padding: 2px;
}
div.stop-dragging {
  -ms-user-select: none;
  -moz-user-select: -moz-none;
  -khtml-user-select: none;
  -webkit-user-select: none;
  user-select: none;
}
.chat-container {
  font-weight: 500;
}
.textarea {
  flex: 1;
  height: 100%;
  padding: 2px;
}

.textarea textarea::-webkit-scrollbar {
  display: none;
}

.textarea textarea {
  border: none;
}
textarea:focus {
  outline: none;
  box-shadow: none !important;
}
.submit {
  padding-top: 20px;
  padding-right: 5px;
  padding-left: 5px;
}

.chat-btn.act {
  position: fixed;
  right: 5vw;
  top: 32vh;
  width: 40px;
  height: 40px;
}

.chat-btn {
  transition: all 0.3s;
  z-index: 10;
  border-radius: 100%;
  width: 60px;
  height: 60px;
  border: 1px solid gray;
  padding: 7px;
  position: relative;
}
</style>
