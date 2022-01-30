package com.slack.slack.domain.api;

import com.slack.slack.common.dto.card.*;
import com.slack.slack.common.entity.Attachment;
import com.slack.slack.common.entity.Card;
import com.slack.slack.domain.service.CardService;
import com.slack.slack.common.exception.ResourceNotFoundException;
import com.slack.slack.common.exception.UnauthorizedException;
import com.slack.slack.common.exception.UserNotFoundException;
import com.slack.slack.common.util.ResponseHeaderManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 카드 컨트롤러
 *
 * @author 김동현
 * @version 1.0, 카드 컨트롤러 생성
 */
@RestController
public class CardController {

    private CardService cardService;

    private ModelMapper modelMapper;

    public CardController(CardService cardService, ModelMapper modelMapper) {
        this.cardService = cardService;
        this.modelMapper = modelMapper;
    }

    @ApiOperation(value = "카드 리스트", notes = "카드 리스트를 봅니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 가입을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "카드에 대한 권한이 없어 조회 할 수 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @GetMapping("/boards/{boardId}/cards")
    public ResponseEntity card_get (
            @ApiParam(value = "보드 아이디", required = true)  @PathVariable Integer boardId
    ) {

        List<CardDTO> cardDTOs = cardService.retrieveCards(boardId);

        return new ResponseEntity(cardDTOs
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.OK);
    }


    @ApiOperation(value = "카드 생성하기", notes = "카드를 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "카드 생성을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PostMapping("/boards/{boardId}/cards")
    public ResponseEntity card_post (
            HttpServletRequest request,
            @PathVariable Integer boardId,
            @ApiParam(value = "카드 정보", required = true) @ModelAttribute CardCommand cardCommand)
    {
        Integer createdCardId = cardService.create(request, boardId, cardCommand);

        return new ResponseEntity(createdCardId
                , ResponseHeaderManager.headerWithOnePath(createdCardId), HttpStatus.CREATED);
    }

    @ApiOperation(value = "카드 수정하기", notes = "카드를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드 수정을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PatchMapping("/boards/{boardId}/cards")
    public ResponseEntity card_patch (
            HttpServletRequest request,
            @PathVariable Integer boardId,
            @ApiParam(value = "카드 정보", required = true) @ModelAttribute CardCommand cardCommand
    ) {
        Integer updatedCardId = cardService.updateCard(request, cardCommand);

        return new ResponseEntity(updatedCardId
                , ResponseHeaderManager.headerWithOnePath(updatedCardId), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "카드 수정하기", notes = "카드를 위치를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드 수정을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PatchMapping("/boards/{boardId}/cards/position")
    public ResponseEntity cardPosotion_patch (
            @PathVariable(value = "boardId") Integer boardId,
            @ApiParam(value = "카드 정보", required = true) @RequestBody CardsCommand cards)
    {

        cardService.updateCardPosition(boardId, cards.getCards());

        return new ResponseEntity(null
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.ACCEPTED);
    }


    @ApiOperation(value = "카드 삭제하기", notes = "카드를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드 삭제를 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @DeleteMapping("/boards/{boardId}/cards/{cardId}")
    public ResponseEntity card_delete (
            @PathVariable Integer boardId,
            @ApiParam(value = "카드 아이디", required = true) @PathVariable Integer cardId)
    {

        CardCommand cardCommand = CardCommand.builder().cardId(cardId).build();

        Integer deletedCardId = cardService.delete(cardCommand);

        return new ResponseEntity(deletedCardId
                , ResponseHeaderManager.headerWithOnePath(deletedCardId), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "파일 업로드", notes = "카드에 파일을 업로드 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "카드에 파일 업로드를 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PostMapping("/boards/{boardId}/cards/files")
    public ResponseEntity file_post (
            HttpServletRequest request,
            @PathVariable Integer boardId,
            @ApiParam(value = "카드 정보", required = true) @ModelAttribute CardCommand cardCommand
    ) {

        cardService.fileUpload(request, boardId, cardCommand.getCardId());

        return new ResponseEntity(null
                , ResponseHeaderManager.headerWithThisPath(), HttpStatus.CREATED);
    }

    @ApiOperation(value = "파일 삭제", notes = "카드 첨부된 파일을 삭제 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드에 첨부된 파일을 삭제 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @DeleteMapping("/boards/{boardId}/cards/files")
    public ResponseEntity file_post (
            @PathVariable Integer boardId,
            @ApiParam(value = "첨부 파일 정보", required = true) @RequestBody AttachmentCommand attachmentCommand
    ) {

        Integer deletedAttachmentId = cardService.deleteFile(boardId, attachmentCommand);

        return new ResponseEntity(deletedAttachmentId
                , ResponseHeaderManager.headerWithOnePath(deletedAttachmentId), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "댓글 생성", notes = "카드에 댓글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "카드에 댓글 생성을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PostMapping("/boards/{boardId}/cards/replies")
    public ResponseEntity reply_post (
            @PathVariable Integer boardId,
            @ApiParam(value = "답글 정보", required = true) @RequestBody ReplyCommand replyCommand
    ) {

        Integer createdCardReplyId = cardService.createCardReply(replyCommand);

        return new ResponseEntity(createdCardReplyId
                , ResponseHeaderManager.headerWithOnePath(createdCardReplyId), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "댓글 수정", notes = "카드에 댓글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "카드의 댓글 수정을 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @PatchMapping("/boards/{boardId}/cards/replies")
    public ResponseEntity reply_patch (
            @PathVariable Integer boardId,
            @ApiParam(value = "댓글 정보", required = true) @RequestBody ReplyCommand replyCommand
    ) {

        Integer updatedCardReplyId = cardService.updateCardReply(replyCommand);

        return new ResponseEntity(updatedCardReplyId
                , ResponseHeaderManager.headerWithOnePath(updatedCardReplyId), HttpStatus.ACCEPTED);
    }

    @ApiOperation(value = "댓글 삭제", notes = "카드에 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "댓글 삭제를 성공 했습니다.")
            , @ApiResponse(responseCode = "401", description = "보드에 대한 권한이 없습니다.") // UnauthorizedException
            , @ApiResponse(responseCode = "404", description = "카드 또는 유저가 없습니다.") // UserNotFoundException
    })
    @DeleteMapping("/boards/{boardId}/cards/replies/{replyId}")
    public ResponseEntity reply_delete(
            @PathVariable Integer boardId,
            @ApiParam(value = "댓글 아이디", required = true) @PathVariable Integer replyId
    ) {

        Integer deletedReplyId = cardService.deleteReply(replyId);

        return new ResponseEntity(deletedReplyId
                , ResponseHeaderManager.headerWithOnePath(deletedReplyId), HttpStatus.ACCEPTED);
    }

}
