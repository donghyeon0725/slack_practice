package com.slack.slack.domain.service;

import com.slack.slack.common.dto.board.BoardDTO;
import com.slack.slack.common.dto.board.BoardReturnDTO;
import com.slack.slack.common.entity.Board;
import com.slack.slack.common.dto.team.TeamDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BoardService {
    /* 보드를 생성합니다. */
    Board create(BoardDTO boardDTO);

    /* 보드를 삭제합니다. */
    Board delete(BoardDTO boardDTO);

    /* 보드의 일부를 수정합니다. */
    Board patchUpdate(BoardDTO boardDTO);

    /* 보드의 배너를 수정합니다. */
    Board patchUpdateBanner(HttpServletRequest request, BoardDTO boardDTO);

    /* 팀의 보드을 보여줍니다. */
    List<BoardReturnDTO> retrieveBoard(TeamDTO teamDTO);
}
