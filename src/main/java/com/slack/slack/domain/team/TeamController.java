package com.slack.slack.domain.team;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.domain.user.User;
import com.slack.slack.domain.user.UserDTO;
import com.slack.slack.error.exception.*;
import com.slack.slack.requestmanager.ResponseFilterManager;
import com.slack.slack.requestmanager.ResponseHeaderManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Locale;

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

    private final SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("name", "description", "date", "state");
    private final SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "email");
    private final SimpleBeanPropertyFilter memberFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "team", "state", "date");
    // 위 필터를 우리가 사용 가능한 형태로 변경. UserInfo 을 대상으로 filter를 적용하겠다는 의미
    private final FilterProvider filters = new SimpleFilterProvider().addFilter("Team", filter).addFilter("User", userFilter).addFilter("TeamMember", memberFilter);



    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * 팀 리스트 모두 불러오기
     *
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * */
    @GetMapping("")
    public ResponseEntity team_get(
            Model model
            , Locale locale
            , @RequestHeader(value = "X-AUTH-TOKEN") String token) throws UserNotFoundException, ResourceConflict {

        List<Team> savedTeam = teamService.retrieveTeam(token);

        return new ResponseEntity(ResponseFilterManager.setFilters(savedTeam, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }

    /**
     * 팀 생성하기
     * 팀은 1개만 생성 가능
     *
     * @ param TeamDTO teamDTO 팀 정보를 받습니다.
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception ResourceConflict : 이미 팀을 생성했을 경우 발생합니다.
     * */
    @PostMapping("")
    public ResponseEntity team_post(
            @RequestBody TeamDTO teamDTO
            , Model model
            , Locale locale
            , @RequestHeader(value = "X-AUTH-TOKEN") String token) throws UserNotFoundException, ResourceConflict {

        Team savedTeam = teamService.save(token, teamDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(savedTeam, filters)
                , ResponseHeaderManager.headerWithOnePath(savedTeam.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 팀 업데이트 하기, 전체 업데이트 기능
     *
     * @ param TeamDTO teamDTO 팀 정보를 받습니다.
     *
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception ResourceNotFoundException : 검색된 팀이 없을 경우 반환합니다.
     * @ exception UnauthorizedException : 권한이 없는 자원입니다.
     * @ exception InvalidInputException : 아이디가 없거나, 값이 잘못된 경우 반환합니다.
     * @ exception ResourceConflict : 이미 팀이 있는 경우 반환합니다.
     * */
    @PutMapping("")
    public ResponseEntity team_put (
            @RequestBody TeamDTO teamDTO
            , Model model
            , Locale locale
            , @RequestHeader(value = "X-AUTH-TOKEN") String token) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException, InvalidInputException {

        Team updatedTeam = teamService.putUpdate(token, teamDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(updatedTeam, filters), ResponseHeaderManager.headerWithOnePath(updatedTeam.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 팀 업데이트 하기, 일부 업데이트 기능
     *
     * @ param TeamDTO teamDTO 팀 정보를 받습니다.
     * @ exception ResourceNotFoundException : 검색된 팀이 없을 경우 반환합니다.
     * @ exception UserNotFoundException : 사용자가 검색되지 않을 경우 반환합니다.
     * @ exception UnauthorizedException : 권한이 없는 자원입니다.
     * @ exception InvalidInputException : 아이디가 없거나, 값이 잘못된 경우 반환합니다.
     * */
    @PatchMapping("")
    public ResponseEntity team_patch(
            @RequestBody TeamDTO teamDTO
            , Model model
            , Locale locale
            , @RequestHeader(value = "X-AUTH-TOKEN") String token) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException, InvalidInputException {

        Team updatedTeam = teamService.patchUpdate(token, teamDTO);
        MappingJacksonValue mapping = new MappingJacksonValue(updatedTeam);
        mapping.setFilters(filters);

        return new ResponseEntity(ResponseFilterManager.setFilters(updatedTeam, filters)
                , ResponseHeaderManager.headerWithOnePath(updatedTeam.getId()), HttpStatus.ACCEPTED);
    }


    /**
     * 팀 초대하기
     *
     * @ param TeamDTO teamDTO 팀 정보를 받습니다.
     * @ exception UnauthorizedException : 팀 생성자가 아닐 경우 반환 합니다.
     * @ exception ResourceNotFoundException : 팀 생성자가 아닙니다.
     * */
    @GetMapping("/invite/{teamId}/{email}")
    public ResponseEntity invite_get(
            @PathVariable Integer teamId
            , @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @PathVariable String email
            , Locale locale) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException {


        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setId(teamId);

        User invited_user = teamService.invite(token, email, teamDTO, locale);

        return new ResponseEntity(ResponseFilterManager.setFilters(invited_user, filters)
                , ResponseHeaderManager.headerWithOnePath(invited_user.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 팀 합류하기
     *
     * @ param TeamDTO teamDTO 팀 정보를 받습니다.
     * @ exception UnauthorizedException : 팀 생성자가 아닐 경우 반환 합니다.
     * @ exception ResourceNotFoundException : 팀 생성자가 아닙니다.
     * */
    @PatchMapping("/join")
    public ResponseEntity join_post(
            @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @RequestBody UserDTO userDTO
            , Locale locale) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException {

        TeamMember member = teamService.accept(token, userDTO.getEmail());

        return new ResponseEntity(ResponseFilterManager.setFilters(member, filters)
                , ResponseHeaderManager.headerWithOnePath(member.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 팀원 강퇴하기
     *
     * @ param TeamDTO teamDTO 팀 정보를 받습니다.
     * @ exception UnauthorizedException : 팀 생성자가 아닐 경우 반환 합니다.
     * @ exception ResourceNotFoundException : 팀 생성자가 아닙니다.
     * */
    @PatchMapping("/kickout")
    public ResponseEntity kickout_patch(
            @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @RequestBody TeamMemberDTO teamMemberDTO
            , Locale locale) throws ResourceNotFoundException, UserNotFoundException, UnauthorizedException {

        TeamMember member = teamService.kickout(token, teamMemberDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(member, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }


}
