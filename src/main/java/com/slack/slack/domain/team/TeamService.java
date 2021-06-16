package com.slack.slack.domain.team;

import com.slack.slack.domain.user.User;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Locale;

public interface TeamService {
    /* 팀을 생성합니다. */
    Team save(String token, TeamDTO teamDTO);

    /* 팀을 삭제합니다. */
    Team delete(String token, TeamDTO teamDTO);

    /* 팀을 수정합니다. */
    Team putUpdate(String token, TeamDTO teamDTO);

    /* 팀의 일부를 수정합니다. */
    Team patchUpdate(String token, TeamDTO teamDTO);


    /* 가입된 팀 리스트를 보여줍니다. */
//    Team retrieveAllTeam(Team team);

    /* 자신의 팀을 보여줍니다. */
    List<Team> retrieveTeam(String token);

    /* 팀원을 보여줍니다. */
    List<TeamMember> retrieveTeamMember(String token, Integer teamId);

    /* 초대 */
    User invite(String token, String to, TeamDTO teamDTO, Locale locale);

    /* 수락 */
    TeamMember accept(String joinToken, String email);

    /* 팀원 강퇴 */
    TeamMember kickout(String token, TeamMemberDTO teamMemberDTO);

    /* 팀 채팅 가져오기 */
    List<TeamChat> retrieveTeamChat(Integer teamId, Integer chatId, Pageable page);

    /* 팀 채팅 삭제하기 */
    TeamChat deleteTeamChat(TeamChatDTO teamChatDTO);

    /* 팀 채팅 추가하기 */
    TeamChat createTeamChat(String token, TeamChatDTO teamChatDTO);
}
