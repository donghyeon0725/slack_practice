package com.slack.slack.domain.card;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.domain.board.BoardDTO;
import com.slack.slack.domain.team.Team;
import com.slack.slack.error.exception.ResourceConflict;
import com.slack.slack.error.exception.ResourceNotFoundException;
import com.slack.slack.error.exception.UnauthorizedException;
import com.slack.slack.error.exception.UserNotFoundException;
import com.slack.slack.requestmanager.ResponseFilterManager;
import com.slack.slack.requestmanager.ResponseHeaderManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

/**
 * 카드 컨트롤러
 *
 * @author 김동현
 * @version 1.0, 카드 컨트롤러 생성
 */
@RestController
@RequestMapping("/card")
public class CardController {
    private final SimpleBeanPropertyFilter boardFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "date", "state", "title", "content");
    private final SimpleBeanPropertyFilter memberFilter = SimpleBeanPropertyFilter.filterOutAllExcept("user");
    private final SimpleBeanPropertyFilter userFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "name", "email");
    private final SimpleBeanPropertyFilter teamFilter = SimpleBeanPropertyFilter.filterOutAllExcept();
    private final SimpleBeanPropertyFilter cardFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "board", "teamMember", "title", "content", "position", "state", "date", "attachments", "replies");

    private final SimpleBeanPropertyFilter replyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("content", "date", "id", "teamMember");
    private final SimpleBeanPropertyFilter attachmentFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "path", "systemFilename", "filename", "description", "date", "state");
    private final SimpleBeanPropertyFilter activityFilter = SimpleBeanPropertyFilter.filterOutAllExcept();
    private final FilterProvider filters = new SimpleFilterProvider()
            .addFilter("Activity", activityFilter).addFilter("Attachment", attachmentFilter)
            .addFilter("Reply", replyFilter).addFilter("Board", boardFilter)
            .addFilter("Team", teamFilter).addFilter("TeamMember", memberFilter)
            .addFilter("Card", cardFilter).addFilter("User", userFilter);

    private CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * 카드 리스트 보기
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "카드 리스트", notes = "카드 리스트를 봅니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 가입을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "카드에 대한 권한이 없어 조회 할 수 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @GetMapping("/{boardId}")
    public ResponseEntity card_get (
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "보드 아이디", required = true)  @PathVariable Integer boardId
    ) throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        List<Card> cards = cardService.retrieveCards(token, boardId);

        return new ResponseEntity(ResponseFilterManager.setFilters(cards, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }


    /**
     * 카드 생성하기
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "카드 생성하기", notes = "카드를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "카드 생성을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PostMapping("")
    public ResponseEntity card_post (
            HttpServletRequest request
            , @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "카드 정보", required = true) @ModelAttribute CardDTO cardDTO)
    throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        Card savedCard = cardService.create(request, token, cardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(savedCard, filters)
                , ResponseHeaderManager.headerWithOnePath(savedCard.getId()), HttpStatus.CREATED);
    }

    /**
     * 카드 수정하기
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "카드 수정하기", notes = "카드를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드 수정을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PatchMapping("")
    public ResponseEntity card_patch (HttpServletRequest request
            , @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "카드 정보", required = true) @ModelAttribute CardDTO cardDTO)
    throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        Card updatedCard = cardService.updateCard(request, token, cardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(updatedCard, filters)
                , ResponseHeaderManager.headerWithOnePath(updatedCard.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 카드 위치 수정하기
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "카드 수정하기", notes = "카드를 위치를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드 수정을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PatchMapping("/position")
    public ResponseEntity cardPosotion_patch (
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "카드 정보", required = true) @RequestBody CardsDTO cards)
            throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        List<Card> updatedCard = cardService.updateCardPosition(token, cards.getCards());

        return new ResponseEntity(ResponseFilterManager.setFilters(updatedCard, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }


    /**
     * 카드 삭제하기
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "카드 삭제하기", notes = "카드를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드 삭제를 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @DeleteMapping("/{id}")
    public ResponseEntity card_delete (
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "카드 아이디", required = true) @PathVariable Integer id)
    throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        CardDTO cardDTO = new CardDTO();
        cardDTO.setId(id);

        Card deletedCard = cardService.delete(token, cardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(deletedCard, filters)
                , ResponseHeaderManager.headerWithOnePath(deletedCard.getId()), HttpStatus.ACCEPTED);
    }


    /**
     * 파일 업로드 하기
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "파일 업로드", notes = "카드에 파일을 업로드 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "카드에 파일 업로드를 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PostMapping("/files")
    public ResponseEntity file_post (
            HttpServletRequest request
            , @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "카드 정보", required = true) @ModelAttribute CardDTO cardDTO)
    throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        List<Attachment> attachments  = cardService.fileUpload(request, token, cardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(attachments, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.CREATED);
    }

    /**
     * 파일 삭제
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "파일 삭제", notes = "카드 첨부된 파일을 삭제 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드에 첨부된 파일을 삭제 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @DeleteMapping("/files")
    public ResponseEntity file_post (
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "첨부 파일 정보", required = true) @RequestBody AttachmentDTO attachmentDTO)
    throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        Attachment attachment  = cardService.deleteFile(token, attachmentDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(attachment, filters)
                , ResponseHeaderManager.headerWithOnePath(attachment.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 댓글 생성
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "댓글 생성", notes = "카드에 댓글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "카드에 댓글 생성을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PostMapping("/replies")
    public ResponseEntity reply_post (
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "답글 정보", required = true) @RequestBody ReplyDTO replyDTO)
    throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        System.out.println(replyDTO.getCardId());

        Reply reply = cardService.createCardReply(token, replyDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(reply, filters)
                , ResponseHeaderManager.headerWithOnePath(reply.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 댓글 수정
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "댓글 수정", notes = "카드에 댓글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드의 댓글 수정을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PatchMapping("/replies")
    public ResponseEntity reply_patch (
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "댓글 정보", required = true) @RequestBody ReplyDTO replyDTO)
    throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        Reply reply = cardService.updateCardReply(token, replyDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(reply, filters)
                , ResponseHeaderManager.headerWithOnePath(reply.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 댓글 삭제
     *
     * @ exception UnauthorizedException : 권한이 없습니다.
     * @ exception UserNotFoundException : 유저를 찾을 수 없습니다.
     * @ exception ResourceNotFoundException : 자원이 존재하지 않습니다.
     * */
    @ApiOperation(value = "댓글 삭제", notes = "카드에 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "댓글 삭제를 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @DeleteMapping("/replies/{id}")
    public ResponseEntity reply_delete(
            @ApiParam(value = "토큰", required = true) @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ApiParam(value = "댓글 아이디", required = true) @PathVariable Integer id)
    throws UnauthorizedException, UserNotFoundException, ResourceNotFoundException {

        ReplyDTO replyDTO = new ReplyDTO();
        replyDTO.setId(id);
        Reply reply = cardService.deleteReply(token,replyDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(reply, filters)
                , ResponseHeaderManager.headerWithOnePath(reply.getId()), HttpStatus.ACCEPTED);
    }

}
