package com.slack.socket.socket.service;

import com.slack.slack.common.dto.team.TeamChatCommand;
import com.slack.slack.common.entity.TeamChat;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    /* 팀 채팅 가져오기 */
    List<TeamChat> retrieveTeamChat(Integer teamId, Integer chatId, Pageable page);

    /* 팀 채팅 삭제하기 */
    Integer deleteTeamChat(Integer teamChatId);

    /* 팀 채팅 추가하기 */
    Integer createTeamChat(TeamChatCommand teamChatCommand);
}
