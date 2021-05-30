package com.slack.slack.domain.card;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.slack.slack.domain.board.BoardDTO;
import com.slack.slack.domain.team.Team;
import com.slack.slack.error.exception.ResourceConflict;
import com.slack.slack.error.exception.UserNotFoundException;
import com.slack.slack.requestmanager.ResponseFilterManager;
import com.slack.slack.requestmanager.ResponseHeaderManager;
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
    private final SimpleBeanPropertyFilter memberFilter = SimpleBeanPropertyFilter.filterOutAllExcept();
    private final SimpleBeanPropertyFilter teamFilter = SimpleBeanPropertyFilter.filterOutAllExcept();
    private final SimpleBeanPropertyFilter cardFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "board", "teamMember", "title", "content", "position", "state", "date", "attachments", "replies");

    private final SimpleBeanPropertyFilter replyFilter = SimpleBeanPropertyFilter.filterOutAllExcept("content", "date", "id");
    private final SimpleBeanPropertyFilter attachmentFilter = SimpleBeanPropertyFilter.filterOutAllExcept("id", "filename", "description", "date", "state");
    private final SimpleBeanPropertyFilter activityFilter = SimpleBeanPropertyFilter.filterOutAllExcept();
    private final FilterProvider filters = new SimpleFilterProvider()
            .addFilter("Activity", activityFilter).addFilter("Attachment", attachmentFilter)
            .addFilter("Reply", replyFilter).addFilter("Board", boardFilter)
            .addFilter("Team", teamFilter).addFilter("TeamMember", memberFilter)
            .addFilter("Card", cardFilter);

    private CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * 카드 리스트 보기
     * */
    @GetMapping("/{boardId}")
    public ResponseEntity card_get (@RequestHeader(value = "X-AUTH-TOKEN") String token, @PathVariable Integer boardId) {


        List<Card> cards = cardService.retrieveCards(token, boardId);

        return new ResponseEntity(ResponseFilterManager.setFilters(cards, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }


    /**
     * 카드 생성하기
     * */
    @PostMapping("")
    public ResponseEntity card_post (HttpServletRequest request, @ModelAttribute CardDTO cardDTO) {

        Card savedCard = cardService.create(request, cardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(savedCard, filters)
                , ResponseHeaderManager.headerWithOnePath(savedCard.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 카드 수정하기
     * */
    @PatchMapping("")
    public ResponseEntity card_patch (HttpServletRequest request
            , @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ModelAttribute CardDTO cardDTO) {

        Card updatedCard = cardService.updateCard(request, token, cardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(updatedCard, filters)
                , ResponseHeaderManager.headerWithOnePath(updatedCard.getId()), HttpStatus.ACCEPTED);
    }


    /**
     * 카드 삭제하기
     * */
    @DeleteMapping("")
    public ResponseEntity card_delete (@RequestHeader(value = "X-AUTH-TOKEN") String token, @RequestBody CardDTO cardDTO) {

        Card deletedCard = cardService.delete(token, cardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(deletedCard, filters)
                , ResponseHeaderManager.headerWithOnePath(deletedCard.getId()), HttpStatus.ACCEPTED);
    }


    /**
     * 파일 업로드 하기
     * */
    @PostMapping("/files")
    public ResponseEntity file_post (HttpServletRequest request
            , @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @ModelAttribute CardDTO cardDTO) {

        List<Attachment> attachments  = cardService.fileUpload(request, token, cardDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(attachments, filters)
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }

    /**
     * 파일 삭제
     * */
    @DeleteMapping("/files")
    public ResponseEntity file_post (@RequestHeader(value = "X-AUTH-TOKEN") String token, @RequestBody AttachmentDTO attachmentDTO) {

        Attachment attachment  = cardService.deleteFile(token, attachmentDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(attachment, filters)
                , ResponseHeaderManager.headerWithOnePath(attachment.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 댓글 생성
     * */
    @PostMapping("/replies")
    public ResponseEntity reply_post (@RequestHeader(value = "X-AUTH-TOKEN") String token, @RequestBody ReplyDTO replyDTO) {

        Reply reply = cardService.createCardReply(token, replyDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(reply, filters)
                , ResponseHeaderManager.headerWithOnePath(reply.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 댓글 수정
     * */
    @PatchMapping("/replies")
    public ResponseEntity reply_patch (
            @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @RequestBody ReplyDTO replyDTO) {

        Reply reply = cardService.updateCardReply(token, replyDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(reply, filters)
                , ResponseHeaderManager.headerWithOnePath(reply.getId()), HttpStatus.ACCEPTED);
    }

    /**
     * 댓글 삭제
     * */
    @DeleteMapping("/replies")
    public ResponseEntity reply_delete(
            @RequestHeader(value = "X-AUTH-TOKEN") String token
            , @RequestBody ReplyDTO replyDTO) {

        Reply reply = cardService.deleteReply(token,replyDTO);

        return new ResponseEntity(ResponseFilterManager.setFilters(reply, filters)
                , ResponseHeaderManager.headerWithOnePath(reply.getId()), HttpStatus.ACCEPTED);
    }

}
