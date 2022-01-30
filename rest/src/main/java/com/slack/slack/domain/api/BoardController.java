package com.slack.slack.domain.api;

import com.slack.slack.common.dto.board.BoardCommand;
import com.slack.slack.common.dto.board.BoardDTO;
import com.slack.slack.common.dto.team.TeamCommand;
import com.slack.slack.domain.service.BoardService;
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

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 보드 컨트롤러
 *
 * @author 김동현
 * @version 1.0, 보드 컨트롤러 생성
 */
@RestController
public class BoardController {

    private BoardService boardService;

    private ModelMapper modelMapper;

    public BoardController(BoardService boardService, ModelMapper modelMapper) {
        this.boardService = boardService;
        this.modelMapper = modelMapper;
    }

    @ApiOperation(value = "보드 리스트 불러오기", notes = "가입된 팀과 자신의 보드를 리스트 형식으로 불러옵니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "보드 리스트를 성공적으로 불러 왔습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
    })
    @GetMapping("/teams/{teamId}/boards")
    public ResponseEntity board_get (
            @PathVariable Integer teamId
    ) {

        TeamCommand teamCommand = TeamCommand.builder().id(teamId).build();

        List<BoardDTO> boardDTOS = boardService.retrieveBoard(teamCommand);

        return new ResponseEntity(boardDTOS
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }

    @ApiOperation(value = "보드 생성하기", notes = "팀에 보드를 생성합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "보드를 성공적으로 생성 했습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
            , @ApiResponse(code = 409, message = "보드를 이미 생성 했습니다.") // ResourceConflict
    })
    @PostMapping("/teams/{teamId}/boards")
    public ResponseEntity board_post(
            @PathVariable Integer teamId,
            @ApiParam(value = "보드 정보", required = true) @RequestBody BoardCommand boardCommand
    ) {

        Integer createdBoardId = boardService.create(teamId, boardCommand);

        return new ResponseEntity(createdBoardId
                , ResponseHeaderManager.headerWithOnePath(createdBoardId), HttpStatus.ACCEPTED);
    }


    @ApiOperation(value = "보드 수정하기", notes = "팀의 보드를 수정합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "보드를 성공적으로 수정 했습니다.")
            , @ApiResponse(code = 400, message = "유저 값이 잘못 되었거나 팀 아이디를 입력하지 않았습니다.") // UnauthorizedException
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
            , @ApiResponse(code = 409, message = "보드를 이미 생성 했습니다.") // ResourceConflict
    })
    @PatchMapping("/boards/{boardId}")
    public ResponseEntity board_patch (
            @PathVariable Integer boardId,
            @ApiParam(value = "보드 정보", required = true) @RequestBody BoardCommand boardCommand
    ) {

        Integer updatedBoardId = boardService.patchUpdate(boardId, boardCommand);

        return new ResponseEntity(updatedBoardId
                , ResponseHeaderManager.headerWithOnePath(updatedBoardId), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "보드 수정하기", notes = "팀의 보드를 수정합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "보드를 성공적으로 수정 했습니다.")
            , @ApiResponse(code = 400, message = "유저 값이 잘못 되었거나 팀 아이디를 입력하지 않았습니다.") // UnauthorizedException
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
            , @ApiResponse(code = 409, message = "보드를 이미 생성 했습니다.") // ResourceConflict
    })
    @PatchMapping("/board/{boardId}/banner")
    public ResponseEntity board_banner_patch (
            @PathVariable Integer boardId,
            HttpServletRequest request,
            @ApiParam(value = "보드 정보", required = true) @ModelAttribute BoardCommand boardCommand
    ) {

        Integer updatedBoardId = boardService.patchUpdateBanner(boardId, boardCommand, request);

        return new ResponseEntity(updatedBoardId
                , ResponseHeaderManager.headerWithOnePath(updatedBoardId), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "보드 삭제하기", notes = "팀의 보드를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(code = 202, message = "보드를 성공적으로 삭제 했습니다.")
            , @ApiResponse(code = 401, message = "팀에 대한 권한이 없습니다. ") // UnauthorizedException
            , @ApiResponse(code = 404, message = "토큰이 잘못되어, 회원을 찾을 수 없습니다.") // UserNotFoundException
    })
    @DeleteMapping("/boards/{boardId}")
    public ResponseEntity board_delete (
            @PathVariable Integer boardId
    ) {
        Integer deletedBoardId = boardService.delete(boardId);

        return new ResponseEntity(deletedBoardId
                , ResponseHeaderManager.headerWithOnePath(deletedBoardId), HttpStatus.ACCEPTED);
    }
}
