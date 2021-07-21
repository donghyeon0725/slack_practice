import { login } from '@/api/auth';
import {
  deleteCookie,
  saveAuthToCookie,
  saveUserEmailToCookie,
} from '@/util/cookie';
import { getTeamMember, getTeams } from '@/api/team';
import { getBoards } from '@/api/board';
import { getCards } from '@/api/card';

const actions = {
  async login({ commit }, userData) {
    // 로그인을 수행해서, 토큰을 받아온다.
    const response = await login(userData);

    // 커밋 작업으로 store에 값을 세팅한다. (또는 쿠키로 저장해두고 페이지를 리로드 처리한다)
    commit('setToken', response.data);
    commit('setLoginEmail', userData.email);

    // 쿠기로 저장한다.
    saveAuthToCookie(response.data);
    saveUserEmailToCookie(userData.email);

    return response;
  },
  logout({ commit }) {
    commit('clearLoginEmail');
    commit('clearLoginToken');

    deleteCookie('til_auth');
    deleteCookie('til_user');
  },
  /* 필요한 값을 페이지 정보의 특정 키워드로 저장 해둡니다. */
  setTeams({ commit }, list) {
    commit('setTeams', list || []);
  },
  setBoards({ commit }, list) {
    commit('setBoards', list || []);
  },
  setCards({ commit }, list) {
    commit('setCards', list || []);
  },
  async refreshTeamsAndEmptyOther(context) {
    let teams = (await getTeams()).data;

    context.commit('setTeams', teams);
    context.commit('clearBoards');
    context.commit('clearCards');
  },
  async refreshOnlyBoards(context, teamId) {
    let boards = (await getBoards(teamId)).data;

    context.commit('setBoards', boards);
  },
  async refreshOnlyCards(context, boardId) {
    let cards = (await getCards(boardId)).data;

    context.commit('setCards', cards);
    context.commit('clearFilterWord');
  },
  async refreshOnlyTeams(context) {
    let teams = (await getTeams()).data;

    context.commit('setTeams', teams);
  },
  async refreshOnlyMembers(context, teamId) {
    let members = (await getTeamMember(teamId)).data;

    context.commit('setMembers', members);
  },
  async refreshSetting(context, teamId) {
    let teams = (await getTeams()).data;

    if (this.$isNotEmpty(teamId)) {
      let boards = (await getBoards(teamId)).data;

      context.commit('setTeams', teams);
      context.commit('setBoards', boards);
    }
  },
  async onCardAdd(context, card) {
    if (context.state.page.cards.filter(s => s.id == card.id).length == 0)
      context.commit('addCard', card);
  },
  async onCardDelete(context, card) {
    context.commit('deleteCard', card);
  },
  async onCardUpdate(context, card) {
    console.log('카드 업데이트 액션!빔');
    context.commit('updateCard', card);
  },
  async onRefreshCards(context, curContext) {
    // 실행 컨텍스트에서 값을 꺼내어 초기화
    let cards = (await getCards(curContext.$route.params.boardId)).data;

    context.commit('setCards', cards);
    context.commit('clearFilterWord');
  },
  async onCardSelected(context, card) {
    let c = context.state.page.cards.filter(s => s.id == card.id)[0];
    c.isSelected = true;
  },
  async onChatAdded(context, chat) {
    if (context.state.page.teamChats.filter(s => s.id == chat.id).length == 0)
      context.commit('addChat', chat);
  },
  async onChatUpdated(context, chat) {
    context.commit('updateChat', chat);
  },
};

export default actions;
