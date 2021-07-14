package com.slack.slack.domain.team;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserDTO;
import com.slack.slack.domain.user.UserReturnDTO;
import com.slack.slack.error.exception.*;
import com.slack.slack.requestmanager.ResponseFilterManager;
import com.slack.slack.requestmanager.ResponseHeaderManager;
import com.slack.slack.system.State;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 팀 컨트롤러
 *
 * @author 김동현
 * @version 1.0, 팀 컨트롤러 생성
 */
@RestController
@RequestMapping("/teams")
public class TeamController {
    /* 팀서비스 */
    private TeamService teamService;

    private ModelMapper modelMapper;

    public TeamController(TeamService teamService, ModelMapper modelMapper) {
        this.teamService = teamService;
        this.modelMapper = modelMapper;

    }

    /**
     * 팀 리스트 모두 불러오기
     *
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * */
    @ApiOperation(value = "팀 리스트", notes = "팀 리스트를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "팀 리스트를 성공적으로 반환 했습니다.")
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
    })
    @GetMapping("")
    public ResponseEntity team_get(
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token) throws UserNotFoundException {

        List<Team> teams = teamService.retrieveTeam(token);

        List<TeamReturnDTO> teamsDTO = teams.stream().map(s -> modelMapper.map(s, TeamReturnDTO.class)).collect(Collectors.toList());

        return new ResponseEntity(teamsDTO
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }

    /**
     * 팀 멤버 리스트 모두 불러오기
     *
     * @ exception UnauthorizedException : 권한이 없는 사용자에게 에러를 반환 합니다.
     * */
    @ApiOperation(value = "팀 멤버 리스트", notes = "팀 멤버 리스트를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "팀 리스트를 성공적으로 반환 했습니다.")
            , @ApiResponse(code = 401, message = "이 자원에 대해 권한이 없습니다.")
    })
    @GetMapping("/members/{id}")
    public ResponseEntity teamMember_get(
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token,
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Integer id
    ) throws UnauthorizedException {

        List<TeamMember> members = teamService.retrieveTeamMember(token, id);

        List<TeamMemberReturnDTO> membersDTO = members.stream().map(s -> modelMapper.map(s, TeamMemberReturnDTO.class)).collect(Collectors.toList());

        return new ResponseEntity(membersDTO
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }

    /**
     * 팀 생성하기
     * 팀은 1개만 생성 가능
     *
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception ResourceConflict : 이미 팀을 생성했을 경우 발생합니다.
     * */
    @ApiOperation(value = "팀 생성하기", notes = "팀을 생성합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "팀이 생성 되었습니다")
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
            , @ApiResponse(code = 409, message = "이미 회원 가입한 이메일 입니다.") // ResourceConflict
    })
    @PostMapping("")
    public ResponseEntity team_post(
            @ApiParam(value = "팀 정보", required = true)  @RequestBody TeamDTO teamDTO
            , @ApiParam(value = "토큰", required = true)  @RequestHeader(value = "X-AUTH-TOKEN") String token) throws UserNotFoundException, ResourceConflict {

        TeamReturnDTO savedTeam = modelMapper.map(teamService.save(token, teamDTO), TeamReturnDTO.class);

        return new ResponseEntity(savedTeam
                , ResponseHeaderManager.headerWithOnePath(savedTeam.getId()), HttpStatus.CREATED);
    }

    /**
     * 팀 업데이트 하기, 전체 업데이트 기능
     *
     * @ exception UserNotFoundException : 유저가 없을 경우 반환합니다.
     * @ exception ResourceNotFoundException : 팀이 검색되지 않았거나 삭제되었을 경우 반환합니다.
     * @ exception InvalidInputException : 아이디가 없거나, 값이 잘못된 경우 반환합니다.
     * @ exception UnauthorizedException : 권한이 없는 자원입니다.
     * */
    @ApiOperation(value = "팀 전체 업데이트", notes = "팀 정보 전체를 업데이트 합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "업데이트에 성공 했습니다.")
            , @ApiResponse(code = 400, message = "토큰 또는 이메일 형식이 잘못 되었거나, 비밀번호 형식이 잘못 되었습니다.") // InvalidInputException
            , @ApiResponse(code = 401, message = "팀을 수정할 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없거나 없는 팀 입니다.")
            , @ApiResponse(code = 409, message = "이미 회원의 팀이 있습니다.") // ResourceConflict
    })
    @PutMapping("")
    public ResponseEntity team_put (
            @ApiParam(value = "팀 정보", required = true) @RequestBody TeamDTO teamDTO
            , @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token) throws UnauthorizedException, ResourceNotFoundException, UserNotFoundException, InvalidInputException {

        TeamReturnDTO updatedTeam = modelMapper.map(teamService.putUpdate(token, teamDTO), TeamReturnDTO.class);

        return new ResponseEntity(updatedTeam, ResponseHeaderManager.headerWithOnePath(updatedTeam.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 팀 업데이트 하기, 일부 업데이트 기능
     *
     * @ exception ResourceNotFoundException : 검색된 팀이 없을 경우 반환합니다.
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception UnauthorizedException : 권한이 없는 자원입니다.
     * @ exception InvalidInputException : 아이디가 없거나, 값이 잘못된 경우 반환합니다.
     * */
    @ApiOperation(value = "팀 일부 업데이트", notes = "팀 정보 일부를 업데이트 합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "업데이트에 성공 했습니다.")
            , @ApiResponse(code = 400, message = "토큰 또는 이메일 형식이 잘못 되었거나, 비밀번호 형식이 잘못 되었습니다.") // InvalidInputException
            , @ApiResponse(code = 401, message = "팀을 수정할 수 있는 권한이 없습니다.")
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없거나 없는 팀 입니다.")
            , @ApiResponse(code = 409, message = "이미 회원의 팀이 있습니다.") // ResourceConflict
    })
    @PatchMapping("")
    public ResponseEntity team_patch(
            @ApiParam(value = "팀 정보", required = true) @RequestBody TeamDTO teamDTO
            , @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException, InvalidInputException {

        TeamReturnDTO updatedTeam = modelMapper.map(teamService.patchUpdate(token, teamDTO), TeamReturnDTO.class);

        return new ResponseEntity(updatedTeam
                , ResponseHeaderManager.headerWithOnePath(updatedTeam.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 팀 삭제하기
     *
     * @ exception UnauthorizedException : 팀 생성자가 아닐 경우 반환 합니다.
     * @ exception ResourceNotFoundException : 팀 생성자가 아닙니다.
     * @ exception UserNotFoundException : 가입된 유저가 아닙니다.
     * */
    @ApiOperation(value = "팀 삭제하기", notes = "팀을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "팀이 삭제 되었습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(code = 404, message = "팀이 없거나, 가입 되지 않은 유저를 초대했습니다.") // UserNotFoundException
    })
    @DeleteMapping("/{teamId}")
    public ResponseEntity team_delete(
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Integer teamId
            , @ApiParam(value = "토큰", required = true)  @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        TeamReturnDTO deletedTeam = modelMapper.map(teamService.delete(token, TeamDTO.builder().id(teamId).build()), TeamReturnDTO.class);

        return new ResponseEntity(deletedTeam
                , ResponseHeaderManager.headerWithOnePath(deletedTeam.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 팀 초대하기
     *
     * @ exception UnauthorizedException : 팀 생성자가 아닐 경우 반환 합니다.
     * @ exception ResourceNotFoundException : 팀 생성자가 아닙니다.
     * @ exception UserNotFoundException : 가입된 유저가 아닙니다.
     * */
    @ApiOperation(value = "유저 초대하기", notes = "유저를 초대 합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 가입을 성공 했습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(code = 404, message = "팀이 없거나, 가입 되지 않은 유저를 초대했습니다.") // UserNotFoundException
    })
    @GetMapping("/invite/{teamId}/{email}")
    public ResponseEntity invite_get(
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Integer teamId
            , @ApiParam(value = "토큰", required = true)  @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "이메일", required = true)  @PathVariable String email
            , Locale locale) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException {


        TeamDTO teamDTO = TeamDTO.builder().id(teamId).build();

        UserReturnDTO invited_user = modelMapper.map(teamService.invite(token, email, teamDTO, locale), UserReturnDTO.class);


        return new ResponseEntity(invited_user
                , ResponseHeaderManager.headerWithOnePath(invited_user.getId()), HttpStatus.OK);
    }

    /**
     * 팀 합류하기
     *
     * @ exception InvalidTokenException : 토큰이 잘못되었을 경우 반환합니다.
     * @ exception ResourceNotFoundException : 팀이 검색되지 않을 경우 반환합니다.
     * @ exception UserNotFoundException : 초대 이메일이 유효하지 않거나 없을 때 경우 반환합니다.
     * */
    @ApiOperation(value = "팀 합류하기", notes = "팀 멤버로 합류합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "멤버 합류에 성공했습니다.")
            , @ApiResponse(code = 401, message = "팁 합류 토큰이 유효하지 않습니다.") // InvalidTokenException
            , @ApiResponse(code = 404, message = "없는 유저에 대해 팀 합류를 요청 했거나 팀을 찾을 수 없습니다.") // UserNotFoundException
    })
    @PatchMapping("/join")
    public ResponseEntity join_post(
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "유저 정보", required = true) @RequestBody UserDTO userDTO
    ) throws ResourceNotFoundException, UserNotFoundException, InvalidTokenException {

        TeamMemberReturnDTO member = modelMapper.map(teamService.accept(token, userDTO.getEmail()), TeamMemberReturnDTO.class);

        return new ResponseEntity(member
                , ResponseHeaderManager.headerWithOnePath(member.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 팀원 강퇴하기
     *
     * @ exception InvalidTokenException : 토큰이 잘못되었을 경우 반환합니다.
     * @ exception ResourceNotFoundException : 팀이 검색되지 않을 경우 반환합니다.
     * @ exception UserNotFoundException : 팀 생성자나, 멤버가 검색되지 않을 경우 반환합니다.
     * @ exception UnauthorizedException : 팀에 대한 권한이 없을 경우 반환합니다.
     * */
    @ApiOperation(value = "팀원 강퇴하기", notes = "팀원을 강퇴합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "강퇴 성공 했습니다.")
            , @ApiResponse(code = 401, message = "팀원을 강퇴할 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(code = 404, message = "유저 또는 팀을 찾을 수 없습니다.") // UserNotFoundException
    })

    @PatchMapping("/kickout")
    public ResponseEntity kickout_patch(
            @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @RequestBody TeamMemberDTO teamMemberDTO
    ) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException {

        TeamMemberReturnDTO member = modelMapper.map(teamService.kickout(token, teamMemberDTO), TeamMemberReturnDTO.class);

        return new ResponseEntity(member
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }

    /**
     * 팀 채팅 불러오기
     * */
    @GetMapping("/chat/{teamId}")
    public ResponseEntity char_get(
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Integer teamId,
            final Pageable pageable
    ) {

        List<TeamChat> chats = teamService.retrieveTeamChat(teamId, null, pageable)
                .stream()
                .map(s -> TeamChat.builder()
                        .id(s.getId())
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

        List<TeamChat> chats = teamService.retrieveTeamChat(teamId, chatId, pageable)
                .stream()
                .map(s -> TeamChat.builder()
                        .id(s.getId())
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

        TeamChatReturnDTO chat = modelMapper.map(teamService.deleteTeamChat(teamChatDTO), TeamChatReturnDTO.class);

        return new ResponseEntity(chat
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }

    /**
     * 팀 채팅 생성하기
     * */
    @PostMapping("/chat")
    public ResponseEntity chat_post(
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token,
            @ApiParam(value = "채팅 정보", required = true) @RequestBody TeamChatDTO teamChatDTO
    ) {

        TeamChatReturnDTO chat = modelMapper.map(teamService.createTeamChat(token, teamChatDTO), TeamChatReturnDTO.class);

        return new ResponseEntity(chat
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }


}
