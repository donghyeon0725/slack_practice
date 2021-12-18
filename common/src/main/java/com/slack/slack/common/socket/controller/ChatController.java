package com.slack.slack.common.socket.controller;

import com.slack.slack.common.code.State;
import com.slack.slack.common.dto.team.TeamChatDTO;
import com.slack.slack.common.dto.team.TeamChatReturnDTO;
import com.slack.slack.common.entity.TeamChat;
import com.slack.slack.common.socket.service.ChatService;
import com.slack.slack.common.util.ResponseHeaderManager;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

// TODO  /teams => /chat 으로 변경하기
@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class ChatController {

    private final ChatService chatService;

    private final ModelMapper modelMapper;

    /**
     * 팀 채팅 불러오기
     * */
    @GetMapping("/chat/{teamId}")
    public ResponseEntity char_get(
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Integer teamId,
            final Pageable pageable
    ) {

        List<TeamChat> chats = chatService.retrieveTeamChat(teamId, null, pageable)
                .stream()
                .map(s -> TeamChat.builder()
                        .teamChatId(s.getTeamChatId())
                        .date(s.getDate())
                        .description(s.getState().equals(State.DELETED) ? State.DELETED.getDescription() : s.getDescription())
                        .state(s.getState())
                        .email(s.getEmail())
                        .team(s.getTeam())
                        .user(s.getUser())
                        .build()
                ).collect(Collectors.toList());

        List<TeamChatReturnDTO> chatDTOs = chats.stream().map(s -> modelMapper.map(s, TeamChatReturnDTO.class)).collect(Collectors.toList());

        return new ResponseEntity(chatDTOs
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }

    /**
     * 팀 채팅 불러오기
     *
     * 커서 charId
     * */
    @GetMapping("/chat/{teamId}/{chatId}")
    public ResponseEntity char_get(
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Integer teamId,
            @ApiParam(value = "채팅 아이디", required = true) @PathVariable Integer chatId,
            final Pageable pageable
    ) {

        List<TeamChat> chats = chatService.retrieveTeamChat(teamId, chatId, pageable)
                .stream()
                .map(s -> TeamChat.builder()
                        .teamChatId(s.getTeamChatId())
                        .date(s.getDate())
                        .description(s.getState().equals(State.DELETED) ? State.DELETED.getDescription() : s.getDescription())
                        .state(s.getState())
                        .email(s.getEmail())
                        .team(s.getTeam())
                        .user(s.getUser())
                        .build()
                ).collect(Collectors.toList());

        List<TeamChatReturnDTO> chatDTOs = chats.stream().map(s -> modelMapper.map(s, TeamChatReturnDTO.class)).collect(Collectors.toList());


        return new ResponseEntity(chatDTOs
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }


    /**
     * 팀 채팅 삭제하기
     * */
    @DeleteMapping("/chat/{chatId}")
    public ResponseEntity chat_delete(
            @ApiParam(value = "채팅 아이디", required = true) @PathVariable Integer chatId
    ) {

        TeamChatDTO teamChatDTO = TeamChatDTO.builder().id(chatId).build();

        TeamChatReturnDTO chat = modelMapper.map(chatService.deleteTeamChat(teamChatDTO), TeamChatReturnDTO.class);

        return new ResponseEntity(chat
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }

    /**
     * 팀 채팅 생성하기
     * */
    @PostMapping("/chat")
    public ResponseEntity chat_post(
            @ApiParam(value = "채팅 정보", required = true) @RequestBody TeamChatDTO teamChatDTO
    ) {

        TeamChatReturnDTO chat = modelMapper.map(chatService.createTeamChat(teamChatDTO), TeamChatReturnDTO.class);

        return new ResponseEntity(chat
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }

}
