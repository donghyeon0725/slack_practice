package com.slack.slack.domain.board;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamDTO;
import com.slack.slack.error.exception.*;
import com.slack.slack.requestmanager.ResponseFilterManager;
import com.slack.slack.requestmanager.ResponseHeaderManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

/**
 * 보드 컨트롤러
 *
 * @author 김동현
 * @version 1.0, 보드 컨트롤러 생성
 */
@RestController
@RequestMapping("/board")
public class BoardController {

    private final SimpleBeanPropertyFilter boardFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "date", "state", "title", "content", "cards", "bannerPath");
    private final SimpleBeanPropertyFilter memberFilter = SimpleBeanPropertyFilter.filterOutAllExcept();
    private final SimpleBeanPropertyFilter teamFilter = SimpleBeanPropertyFilter.filterOutAllExcept();

    private final SimpleBeanPropertyFilter cardFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "title", "content", "position", "state", "date", "attachments", "replies");
    private final SimpleBeanPropertyFilter replyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("content", "date", "id");
    private final SimpleBeanPropertyFilter attachmentFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "filename", "description", "date", "state");
    private final SimpleBeanPropertyFilter activityFilter = SimpleBeanPropertyFilter.filterOutAllExcept();

    private final FilterProvider filters = new SimpleFilterProvider()
            .addFilter("Board", boardFilter).addFilter("Team", teamFilter).addFilter("TeamMember", memberFilter)
            .addFilter("Card", cardFilter).addFilter("Activity", activityFilter).addFilter("Attachment", attachmentFilter)
            .addFilter("Reply", replyFilter);

    private BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @ApiOperation(value = "보드 리스트 불러오기", notes = "가입된 팀과 자신의 보드를 리스트 형식으로 불러옵니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "보드 리스트를 성공적으로 불러 왔습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
    })
    @GetMapping("/{id}")
    public ResponseEntity board_get (
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Integer id
            , @ApiParam(value = "토큰", required = true)  @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setId(id);

        List<Board> boards = boardService.retrieveBoard(token, teamDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(boards, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }

    @ApiOperation(value = "보드 생성하기", notes = "팀에 보드를 생성합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "보드를 성공적으로 생성 했습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
            , @ApiResponse(code = 409, message = "보드를 이미 생성 했습니다.") // ResourceConflict
    })
    @PostMapping("")
    public ResponseEntity board_post(
            @ApiParam(value = "보드 정보", required = true) @RequestBody BoardDTO boardDTO
            , @ApiParam(value = "보드 정보", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, ResourceConflict {

        Board board = boardService.create(token, boardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(board, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }


    @ApiOperation(value = "보드 수정하기", notes = "팀의 보드를 수정합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "보드를 성공적으로 수정 했습니다.")
            , @ApiResponse(code = 400, message = "유저 값이 잘못 되었거나 팀 아이디를 입력하지 않았습니다.") // UnauthorizedException
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
            , @ApiResponse(code = 409, message = "보드를 이미 생성 했습니다.") // ResourceConflict
    })
    @PatchMapping("")
    public ResponseEntity board_patch (
            @ApiParam(value = "보드 정보", required = true) @RequestBody BoardDTO boardDTO
            , @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, InvalidInputException {

        Board board = boardService.patchUpdate(token, boardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(board, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "보드 수정하기", notes = "팀의 보드를 수정합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "보드를 성공적으로 수정 했습니다.")
            , @ApiResponse(code = 400, message = "유저 값이 잘못 되었거나 팀 아이디를 입력하지 않았습니다.") // UnauthorizedException
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
            , @ApiResponse(code = 409, message = "보드를 이미 생성 했습니다.") // ResourceConflict
    })
    @PatchMapping("/banner")
    public ResponseEntity board_banner_patch (
            HttpServletRequest request
            , @ApiParam(value = "보드 정보", required = true) @ModelAttribute BoardDTO boardDTO
            , @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, InvalidInputException {

        Board board = boardService.patchUpdateBanner(request, token, boardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(board, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "보드 삭제하기", notes = "팀의 보드를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "보드를 성공적으로 삭제 했습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
    })
    @DeleteMapping("/{id}")
    public ResponseEntity board_delete (
            @ApiParam(value = "보드 정보", required = true) @PathVariable Integer id
            , @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(id);

        Board board = boardService.delete(token, boardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(board, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }
}
