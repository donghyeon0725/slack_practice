package com.slack.slack.domain.service;

import com.slack.slack.common.dto.team.TeamDTO;
import com.slack.slack.common.dto.team.TeamMemberDTO;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamChat;
import com.slack.slack.common.entity.TeamMember;
import com.slack.slack.common.entity.User;
import com.slack.slack.common.dto.team.TeamChatDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Locale;

public interface TeamService {
    /* 팀을 생성합니다. */
    Team save(TeamDTO teamDTO);

    /* 팀을 삭제합니다. */
    Team delete(TeamDTO teamDTO);

    /* 팀을 수정합니다. */
    Team putUpdate(TeamDTO teamDTO);

    /* 팀의 일부를 수정합니다. */
    Team patchUpdate(TeamDTO teamDTO);


    /* 가입된 팀 리스트를 보여줍니다. */
//    Team retrieveAllTeam(Team team);

    /* 자신의 팀을 보여줍니다. */
    List<Team> retrieveTeam();

    /* 팀원을 보여줍니다. */
    List<TeamMember> retrieveTeamMember(Integer teamId);

    /* 초대 */
    User invite(String to, TeamDTO teamDTO, Locale locale);

    /* 수락 */
    TeamMember accept(String joinToken, String email);

    /* 팀원 강퇴 */
    TeamMember kickout(TeamMemberDTO teamMemberDTO);

    /* 팀 채팅 가져오기 */
    List<TeamChat> retrieveTeamChat(Integer teamId, Integer chatId, Pageable page);

    /* 팀 채팅 삭제하기 */
    TeamChat deleteTeamChat(TeamChatDTO teamChatDTO);

    /* 팀 채팅 추가하기 */
    TeamChat createTeamChat(TeamChatDTO teamChatDTO);
}
