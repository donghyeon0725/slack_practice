import { getInstanceWithAuth, getInstance } from '@/api/index';

const instance = getInstanceWithAuth();
const instanceNoAuth = getInstance();

// 팀 리스트 요청
function getTeams() {
  return instance.get('teams');
}

// 팀 생성하기
function createTeam(teamData) {
  return instance.post('teams', teamData);
}

// 팀 수정하기
function editTeam(teamData) {
  return instance.patch('teams', teamData);
}

// 팀 삭제하기
function deleteTeam(id) {
  return instance.delete(`teams/${id}`);
}

// 팀 초대하기
function inviteTeam(teamId, email) {
  return instance.get(`teams/invite/${teamId}/${email}`);
}

// 팀 초대 수락하기
function acceptInvite(email, token) {
  return instanceNoAuth.patch(
    'teams/join',
    { email: email },
    {
      headers: {
        Authorization: `bearer ${token}`,
      },
    },
  );
}

// 팀 멤버 불러오기
function getTeamMember(teamId) {
  return instance.get(`teams/members/${teamId}`);
}

// 채팅 불러오기
function getTeamChat(page, size, teamId) {
  return instance.get(`teams/chat/${teamId}?page=${page}&size=${size}`);
}

// 채팅 삭제하기
function deleteTeamChat(chatId) {
  return instance.delete(`teams/chat/${chatId}`);
}

// 채팅 생성하기
function createTeamChat(chatData) {
  return instance.post('teams/chat', chatData);
}

export {
  getTeams,
  createTeam,
  editTeam,
  deleteTeam,
  inviteTeam,
  acceptInvite,
  getTeamMember,
  getTeamChat,
  deleteTeamChat,
  createTeamChat,
};
