package com.slack.slack.domain.service;

import com.slack.slack.common.dto.card.AttachmentCommand;
import com.slack.slack.common.dto.card.CardCommand;
import com.slack.slack.common.dto.card.CardDTO;
import com.slack.slack.common.dto.card.ReplyCommand;
import com.slack.slack.common.entity.Attachment;
import com.slack.slack.common.entity.Card;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CardService {
    /* 카드를 생성합니다. */
    Integer create(HttpServletRequest request, Integer boardId, CardCommand cardCommand);

    /* 카드를 수정합니다. */
    Integer updateCard(HttpServletRequest request, CardCommand cardCommand);

    /* 카드의 포지션을 수정합니다. */
    void updateCardPosition(Integer boardId, List<CardCommand> cardCommandList);

    /* 카드를 삭제합니다. */
    Integer delete(CardCommand cardCommand);

    /* 카드 리스트를 봅니다. */
    List<CardDTO> retrieveCards(Integer boardId);

    /* 파일 첨부하기 */
    void fileUpload(HttpServletRequest request, Integer boardId, Integer cardId);

    /* 첨부 파일 삭제하기 */
    Integer deleteFile(Integer boardId, AttachmentCommand attachmentCommand);

    /* 댓글을 수정합니다. */
    Integer updateCardReply(ReplyCommand replyCommand);

    /* 댓글 생성하기 */
    Integer createCardReply(ReplyCommand replyCommand);

    /* 댓글 삭제하기 */
    Integer deleteReply(Integer replyId);


}
