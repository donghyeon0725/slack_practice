package com.slack.slack.domain.card;

import com.slack.slack.domain.board.BoardDTO;
import com.slack.slack.domain.team.TeamDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface CardService {
    /* 카드를 생성합니다. */
    Card create(HttpServletRequest request, CardDTO cardDTO);

    /* 카드를 수정합니다. */
    Card updateCard(HttpServletRequest request, CardDTO cardDTO);

    /* 카드의 포지션을 수정합니다. */
    List<Card> updateCardPosition(List<CardDTO> cardDTOList);

    /* 카드를 삭제합니다. */
    Card delete(CardDTO cardDTO);

    /* 카드 리스트를 봅니다. */
    List<CardReturnDTO> retrieveCards(Integer boardId);

    /* 파일 첨부하기 */
    List<Attachment> fileUpload(HttpServletRequest request, CardDTO cardDTO);

    /* 첨부 파일 삭제하기 */
    Attachment deleteFile(AttachmentDTO attachmentDTO);

    /* 댓글을 수정합니다. */
    Reply updateCardReply(ReplyDTO replyDTO);

    /* 댓글 생성하기 */
    Reply createCardReply(ReplyDTO replyDTO);

    /* 댓글 삭제하기 */
    Reply deleteReply(ReplyDTO replyDTO);


}
