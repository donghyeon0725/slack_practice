package com.slack.slack.domain.service;

import com.slack.slack.common.dto.team.TeamCommand;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamMember;

import java.util.List;
import java.util.Locale;

public interface TeamService {
    /* 팀을 생성합니다. */
    Integer save(TeamCommand teamCommand);

    /* 팀을 삭제합니다. */
    Integer delete(TeamCommand teamCommand);

    /* 팀을 수정합니다. */
    Integer putUpdate(Integer teamId, TeamCommand teamCommand);

    /* 팀의 일부를 수정합니다. */
    Integer patchUpdate(Integer teamId, TeamCommand teamCommand);


    /* 가입된 팀 리스트를 보여줍니다. */
//    Team retrieveAllTeam(Team team);

    /* 자신의 팀을 보여줍니다. */
    List<Team> retrieveTeam();

    /* 팀원을 보여줍니다. */
    List<TeamMember> retrieveTeamMember(Integer teamId);

    /* 초대 */
    Integer invite(Integer teamId, String to, Locale locale);

    /* 수락 */
    Integer accept(String joinToken, String email);

    /* 팀원 강퇴 */
    Integer kickout(Integer teamId, Integer teamMemberId);
}
