package com.slack.slack.domain.board;

import com.slack.slack.domain.team.TeamDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface BoardService {
    /* 보드를 생성합니다. */
    Board create(String token, BoardDTO boardDTO);

    /* 보드를 삭제합니다. */
    Board delete(String token, BoardDTO boardDTO);

    /* 보드의 일부를 수정합니다. */
    Board patchUpdate(String token, BoardDTO boardDTO);

    /* 보드의 배너를 수정합니다. */
    Board patchUpdateBanner(HttpServletRequest request, String token, BoardDTO boardDTO);

    /* 팀의 보드을 보여줍니다. */
    List<Board> retrieveBoard(String token, TeamDTO teamDTO);
}
