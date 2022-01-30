package com.slack.slack.domain.api;

import com.slack.slack.common.dto.team.*;
import com.slack.slack.common.dto.user.UserCommand;
import com.slack.slack.common.entity.Team;
import com.slack.slack.common.entity.TeamMember;
import com.slack.slack.common.dto.user.UserDTO;
import com.slack.slack.domain.service.TeamService;
import com.slack.slack.common.exception.*;
import com.slack.slack.common.util.ResponseHeaderManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
public class TeamController {
    /* 팀서비스 */
    private TeamService teamService;

    private ModelMapper modelMapper;

    public TeamController(TeamService teamService, ModelMapper modelMapper) {
        this.teamService = teamService;
        this.modelMapper = modelMapper;
    }

    @ApiOperation(value = "팀 리스트", notes = "팀 리스트를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "팀 리스트를 성공적으로 반환 했습니다.")
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
    })
    @GetMapping("/teams")
    public ResponseEntity team_get() {

        List<Team> teams = teamService.retrieveTeam();

        List<TeamDTO> teamsDTO = teams.stream().map(TeamDTO::new).collect(Collectors.toList());

        return new ResponseEntity(teamsDTO
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }


    @ApiOperation(value = "팀 멤버 리스트", notes = "팀 멤버 리스트를 불러옵니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "팀 리스트를 성공적으로 반환 했습니다.")
            , @ApiResponse(code = 401, message = "이 자원에 대해 권한이 없습니다.")
    })
    @GetMapping("/teams/{teamId}/members")
    public ResponseEntity teamMember_get(
            @PathVariable Integer teamId
    ) {

        List<TeamMember> members = teamService.retrieveTeamMember(teamId);

        List<TeamMemberDTO> membersDTO = members.stream().map(TeamMemberDTO::new).collect(Collectors.toList());

        return new ResponseEntity(membersDTO
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }


    @ApiOperation(value = "팀 생성하기", notes = "팀을 생성합니다.")
    @ApiResponses({
            @ApiResponse(code = 201, message = "팀이 생성 되었습니다")
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
            , @ApiResponse(code = 409, message = "이미 회원 가입한 이메일 입니다.") // ResourceConflict
    })
    @PostMapping("/teams")
    public ResponseEntity team_post(
            @ApiParam(value = "팀 정보", required = true) @RequestBody TeamCommand teamCommand) {

        Integer savedTeamId = teamService.save(teamCommand);

        return new ResponseEntity(savedTeamId
                , ResponseHeaderManager.headerWithOnePath(savedTeamId), HttpStatus.CREATED);
    }


    @ApiOperation(value = "팀 전체 업데이트", notes = "팀 정보 전체를 업데이트 합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "업데이트에 성공 했습니다.")
            , @ApiResponse(code = 400, message = "토큰 또는 이메일 형식이 잘못 되었거나, 비밀번호 형식이 잘못 되었습니다.") // InvalidInputException
            , @ApiResponse(code = 401, message = "팀을 수정할 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없거나 없는 팀 입니다.")
            , @ApiResponse(code = 409, message = "이미 회원의 팀이 있습니다.") // ResourceConflict
    })
    @PutMapping("/team/{teamId}")
    public ResponseEntity team_put (
            @PathVariable Integer teamId,
            @ApiParam(value = "팀 정보", required = true) @RequestBody TeamCommand teamCommand) {

        Integer updatedTeamId = teamService.putUpdate(teamId, teamCommand);

        return new ResponseEntity(updatedTeamId, ResponseHeaderManager.headerWithOnePath(updatedTeamId), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "팀 일부 업데이트", notes = "팀 정보 일부를 업데이트 합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "업데이트에 성공 했습니다.")
            , @ApiResponse(code = 400, message = "토큰 또는 이메일 형식이 잘못 되었거나, 비밀번호 형식이 잘못 되었습니다.") // InvalidInputException
            , @ApiResponse(code = 401, message = "팀을 수정할 수 있는 권한이 없습니다.")
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없거나 없는 팀 입니다.")
            , @ApiResponse(code = 409, message = "이미 회원의 팀이 있습니다.") // ResourceConflict
    })
    @PatchMapping("/teams/{teamId}")
    public ResponseEntity team_patch(
            @PathVariable Integer teamId,
            @ApiParam(value = "팀 정보", required = true) @RequestBody TeamCommand teamCommand
    ) {

        Integer updatedTeamId = teamService.patchUpdate(teamId, teamCommand);

        return new ResponseEntity(updatedTeamId
                , ResponseHeaderManager.headerWithOnePath(updatedTeamId), HttpStatus.ACCEPTED);
    }


    @ApiOperation(value = "팀 삭제하기", notes = "팀을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "팀이 삭제 되었습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(code = 404, message = "팀이 없거나, 가입 되지 않은 유저를 초대했습니다.") // UserNotFoundException
    })
    @DeleteMapping("/teams/{teamId}")
    public ResponseEntity team_delete(
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Integer teamId
    ) {

        Integer deletedTeamId = teamService.delete(TeamCommand.builder().id(teamId).build());

        return new ResponseEntity(deletedTeamId
                , ResponseHeaderManager.headerWithOnePath(deletedTeamId), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "유저 초대하기", notes = "유저를 초대 합니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "회원 가입을 성공 했습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(code = 404, message = "팀이 없거나, 가입 되지 않은 유저를 초대했습니다.") // UserNotFoundException
    })
    @GetMapping("/teams/{teamId}/invite/{email}")
    public ResponseEntity invite_get(
            @PathVariable Integer teamId
            , @PathVariable String email
            , Locale locale
    ) {

        Integer invitedUserId = teamService.invite(teamId, email, locale);

        return new ResponseEntity(invitedUserId
                , ResponseHeaderManager.headerWithOnePath(invitedUserId), HttpStatus.OK);
    }


    @ApiOperation(value = "팀 합류하기", notes = "팀 멤버로 합류합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "멤버 합류에 성공했습니다.")
            , @ApiResponse(code = 401, message = "팁 합류 토큰이 유효하지 않습니다.") // InvalidTokenException
            , @ApiResponse(code = 404, message = "없는 유저에 대해 팀 합류를 요청 했거나 팀을 찾을 수 없습니다.") // UserNotFoundException
    })
    @PatchMapping("/teams/join")
    public ResponseEntity join_post(
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "Authorization") String token
            , @ApiParam(value = "유저 정보", required = true) @RequestBody UserCommand userCommand
    ) {

        Integer acceptTeamMemberId = teamService.accept(token, userCommand.getEmail());

        return new ResponseEntity(acceptTeamMemberId
                , ResponseHeaderManager.headerWithOnePath(acceptTeamMemberId), HttpStatus.ACCEPTED);
    }


    @ApiOperation(value = "팀원 강퇴하기", notes = "팀원을 강퇴합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "강퇴 성공 했습니다.")
            , @ApiResponse(code = 401, message = "팀원을 강퇴할 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(code = 404, message = "유저 또는 팀을 찾을 수 없습니다.") // UserNotFoundException
    })
    @PatchMapping("/teams/kickout")
    public ResponseEntity kickout_patch(
            @PathVariable Integer teamId,
            @PathVariable Integer teamMemberId
    ) {

        Integer kickoutedTeamMemberId = teamService.kickout(teamId, teamMemberId);

        return new ResponseEntity(kickoutedTeamMemberId
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }
}
