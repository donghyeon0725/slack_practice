package com.slack.slack.common.socket.service.impl;

import com.slack.slack.common.code.ErrorCode;
import com.slack.slack.common.dto.team.TeamChatDTO;
import com.slack.slack.common.entity.*;
import com.slack.slack.common.exception.ResourceNotFoundException;
import com.slack.slack.common.exception.UserNotFoundException;
import com.slack.slack.common.repository.TeamChatRepository;
import com.slack.slack.common.repository.TeamRepository;
import com.slack.slack.common.repository.UserRepository;
import com.slack.slack.common.response.CursorResult;
import com.slack.slack.common.socket.service.ChatService;
import com.slack.slack.common.util.SuccessAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;

    private final TeamRepository teamRepository;

    private final TeamChatRepository teamChatRepository;

    CursorResult<Board> getChat(Integer teamId, Integer chatId, Pageable page) {
        final List<TeamChat> chats = retrieveTeamChat(teamId, chatId, page);
        final Integer lastIdOfList = chats.isEmpty() ?
                null : chats.get(chats.size() - 1).getId();

        return new CursorResult(chats, hasNext(lastIdOfList));
    }

    private Boolean hasNext(Integer id) {
        if (id == null) return false;
        return teamChatRepository.existsByIdLessThan(id);
    }

    @Transactional
    @Override
    public List<TeamChat> retrieveTeamChat(Integer teamId, Integer chatId, Pageable page) {
        return chatId == null ?
                teamChatRepository.findAllByTeamOrderByIdDesc(teamId, page).stream().sorted(Comparator.comparingInt(TeamChat::getId)).collect(Collectors.toList()) :
                teamChatRepository.findByTeamWhereIdLessThanOrderByIdDesc(teamId, chatId, page).stream().sorted(Comparator.comparingInt(TeamChat::getId)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public TeamChat deleteTeamChat(TeamChatDTO teamChatDTO) {
        TeamChat teamChat = teamChatRepository.findById(teamChatDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamChat chat = teamChat.delete();

        return chat;
    }

    @Transactional
    @Override
    public TeamChat createTeamChat(TeamChatDTO teamChatDTO) {
        User user = userRepository.findByEmail(SuccessAuthentication.getPrincipal(String.class))
                .orElseThrow(() -> new UserNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        Team team = teamRepository.findById(teamChatDTO.getTeamId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));

        TeamChat chat = TeamChat.builder()
                .email(user.getEmail())
                .date(new Date())
                .description(teamChatDTO.getDescription())
                .team(team)
                .user(user)
                .baseCreateEntity(BaseCreateEntity.now(user.getEmail()))
                .build();

        chat.place();

        teamChatRepository.save(chat);

        return chat;
    }
}
