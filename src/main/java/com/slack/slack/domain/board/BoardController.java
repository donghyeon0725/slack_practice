package com.slack.slack.domain.board;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.domain.team.Team;
import com.slack.slack.domain.team.TeamDTO;
import com.slack.slack.error.exception.ResourceConflict;
import com.slack.slack.error.exception.UserNotFoundException;
import com.slack.slack.requestmanager.ResponseFilterManager;
import com.slack.slack.requestmanager.ResponseHeaderManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    private final SimpleBeanPropertyFilter boardFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "date", "state", "title", "content", "cards");
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

    /**
     * 보드 리스트 모두 불러오기
     * */
    @GetMapping("/{id}")
    public ResponseEntity board_get(
            @PathVariable Integer id
            , Locale locale
            , @RequestHeader(value = "X-AUTH-TOKEN") String token
            ) throws UserNotFoundException, ResourceConflict {

        TeamDTO teamDTO = new TeamDTO();
        teamDTO.setId(id);

        List<Board> boards = boardService.retrieveBoard(token, teamDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(boards, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }


    /**
     * 보드 리스트 모두 불러오기
     * */
    @PostMapping("")
    public ResponseEntity board_post(
            @RequestBody BoardDTO boardDTO
            , Locale locale
            , @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, ResourceConflict {

        Board board = boardService.create(token, boardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(board, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }


    /**
     * 보드를 수정합니다.
     * */
    @PatchMapping("")
    public ResponseEntity board_patch (
            @RequestBody BoardDTO boardDTO
            , Locale locale
            , @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, ResourceConflict {

        Board board = boardService.patchUpdate(token, boardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(board, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }


    /**
     * 보드를 삭제합니다.
     * */
    @DeleteMapping("")
    public ResponseEntity board_delete (
            @RequestBody BoardDTO boardDTO
            , Locale locale
            , @RequestHeader(value = "X-AUTH-TOKEN") String token
    ) throws UserNotFoundException, ResourceConflict {

        Board board = boardService.delete(token, boardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(board, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }
}
