package com.slack.slack.domain.service;

import com.slack.slack.common.dto.card.AttachmentDTO;
import com.slack.slack.common.dto.card.CardDTO;
import com.slack.slack.common.dto.card.CardReturnDTO;
import com.slack.slack.common.dto.card.ReplyDTO;
import com.slack.slack.common.entity.Attachment;
import com.slack.slack.common.entity.Card;
import com.slack.slack.common.entity.Reply;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CardService {
    /* 카드를 생성합니다. */
    Integer create(HttpServletRequest request, Integer boardId, CardDTO cardDTO);

    /* 카드를 수정합니다. */
    Integer updateCard(HttpServletRequest request, CardDTO cardDTO);

    /* 카드의 포지션을 수정합니다. */
    List<Card> updateCardPosition(Integer boardId, List<CardDTO> cardDTOList);

    /* 카드를 삭제합니다. */
    Integer delete(CardDTO cardDTO);

    /* 카드 리스트를 봅니다. */
    List<CardReturnDTO> retrieveCards(Integer boardId);

    /* 파일 첨부하기 */
    List<Attachment> fileUpload(HttpServletRequest request, Integer boardId, CardDTO cardDTO);

    /* 첨부 파일 삭제하기 */
    Integer deleteFile(Integer boardId, AttachmentDTO attachmentDTO);

    /* 댓글을 수정합니다. */
    Integer updateCardReply(ReplyDTO replyDTO);

    /* 댓글 생성하기 */
    Integer createCardReply(ReplyDTO replyDTO);

    /* 댓글 삭제하기 */
    Integer deleteReply(ReplyDTO replyDTO);


}
