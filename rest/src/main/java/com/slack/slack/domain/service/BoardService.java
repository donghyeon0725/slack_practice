package com.slack.slack.domain.service;

import com.slack.slack.common.dto.board.BoardCommand;
import com.slack.slack.common.dto.board.BoardDTO;
import com.slack.slack.common.dto.team.TeamCommand;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BoardService {
    /* 보드를 생성합니다. */
    Integer create(Integer teamId, BoardCommand boardCommand);

    /* 보드를 삭제합니다. */
    Integer delete(Integer boardId);

    /* 보드의 일부를 수정합니다. */
    Integer patchUpdate(Integer boardId, BoardCommand boardCommand);

    /* 보드의 배너를 수정합니다. */
    Integer patchUpdateBanner(Integer boardId, BoardCommand boardCommand, HttpServletRequest request);

    /* 팀의 보드을 보여줍니다. */
    List<BoardDTO> retrieveBoard(TeamCommand teamCommand);
}
