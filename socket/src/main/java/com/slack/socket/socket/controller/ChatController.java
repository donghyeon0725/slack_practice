package com.slack.socket.socket.controller;

import com.slack.slack.common.code.Status;
import com.slack.slack.common.dto.team.TeamChatCommand;
import com.slack.slack.common.dto.team.TeamChatDTO;
import com.slack.slack.common.entity.TeamChat;
import com.slack.socket.socket.service.ChatService;
import com.slack.slack.common.util.ResponseHeaderManager;
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
            @PathVariable Integer teamId,
            final Pageable pageable
    ) {

        List<TeamChat> chats = chatService.retrieveTeamChat(teamId, null, pageable)
                .stream()
                .map(s -> TeamChat.builder()
                        .teamChatId(s.getTeamChatId())
                        .date(s.getDate())
                        .description(s.getStatus().equals(Status.DELETED) ? Status.DELETED.getDescription() : s.getDescription())
                        .status(s.getStatus())
                        .email(s.getEmail())
                        .team(s.getTeam())
                        .user(s.getUser())
                        .build()
                ).collect(Collectors.toList());

        List<TeamChatDTO> chatDTOs = chats.stream().map(TeamChatDTO::new).collect(Collectors.toList());

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
            @PathVariable Integer teamId,
            @PathVariable Integer chatId,
            final Pageable pageable
    ) {

        List<TeamChat> chats = chatService.retrieveTeamChat(teamId, chatId, pageable)
                .stream()
                .map(s -> TeamChat.builder()
                        .teamChatId(s.getTeamChatId())
                        .date(s.getDate())
                        .description(s.getStatus().equals(Status.DELETED) ? Status.DELETED.getDescription() : s.getDescription())
                        .status(s.getStatus())
                        .email(s.getEmail())
                        .team(s.getTeam())
                        .user(s.getUser())
                        .build()
                ).collect(Collectors.toList());

        List<TeamChatDTO> chatDTOs = chats.stream().map(TeamChatDTO::new).collect(Collectors.toList());


        return new ResponseEntity(chatDTOs
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }


    /**
     * 팀 채팅 삭제하기
     * */
    @DeleteMapping("/chat/{chatId}")
    public ResponseEntity chat_delete(
            @PathVariable Integer chatId
    ) {

        Integer teamChatId = chatService.deleteTeamChat(chatId);

        return new ResponseEntity(teamChatId
                , ResponseHeaderManager.headerWithOnePath(teamChatId), HttpStatus.OK);
    }

    /**
     * 팀 채팅 생성하기
     * */
    @PostMapping("/chat")
    public ResponseEntity chat_post(
            @RequestBody TeamChatCommand teamChatCommand
    ) {

        Integer teamChatId = chatService.createTeamChat(teamChatCommand);

        return new ResponseEntity(teamChatId
                , ResponseHeaderManager.headerWithOnePath(teamChatId), HttpStatus.OK);
    }

}
