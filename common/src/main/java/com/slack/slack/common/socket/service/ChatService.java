package com.slack.slack.common.socket.service;

import com.slack.slack.common.dto.team.TeamChatDTO;
import com.slack.slack.common.entity.TeamChat;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {

    /* 팀 채팅 가져오기 */
    List<TeamChat> retrieveTeamChat(Integer teamId, Integer chatId, Pageable page);

    /* 팀 채팅 삭제하기 */
    TeamChat deleteTeamChat(TeamChatDTO teamChatDTO);

    /* 팀 채팅 추가하기 */
    TeamChat createTeamChat(TeamChatDTO teamChatDTO);
}
