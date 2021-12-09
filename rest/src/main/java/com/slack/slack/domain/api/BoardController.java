package com.slack.slack.domain.api;

import com.slack.slack.common.dto.board.BoardDTO;
import com.slack.slack.common.dto.board.BoardReturnDTO;
import com.slack.slack.common.dto.team.TeamDTO;
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
@RequestMapping("/board")
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
    @GetMapping("/{id}")
    public ResponseEntity board_get (
            @ApiParam(value = "팀 아이디", required = true) @PathVariable Integer id
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        TeamDTO teamDTO = TeamDTO.builder().id(id).build();

        List<BoardReturnDTO> boardReturnDTOs = boardService.retrieveBoard(teamDTO);

        return new ResponseEntity(boardReturnDTOs
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
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, ResourceConflict {

        BoardReturnDTO board = modelMapper.map(boardService.create(boardDTO), BoardReturnDTO.class);

        return new ResponseEntity(board
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
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, InvalidInputException {

        BoardReturnDTO board = modelMapper.map(boardService.patchUpdate(boardDTO), BoardReturnDTO.class);

        return new ResponseEntity(board
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
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException, InvalidInputException {

        BoardReturnDTO board = modelMapper.map(boardService.patchUpdateBanner(request, boardDTO), BoardReturnDTO.class);

        return new ResponseEntity(board
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
    ) throws UserNotFoundException, ResourceNotFoundException, UnauthorizedException {

        BoardDTO boardDTO = BoardDTO.builder().id(id).build();

        BoardReturnDTO board = modelMapper.map(boardService.delete(boardDTO), BoardReturnDTO.class);

        return new ResponseEntity(board
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }
}
