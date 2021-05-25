package com.slack.slack.domain.team;

import com.slack.slack.domain.user.UserDTO;
import com.slack.slack.error.exception.ResourceConflict;
import com.slack.slack.error.exception.UserNotFoundException;

import java.io.IOException;
import java.util.List;

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
}
