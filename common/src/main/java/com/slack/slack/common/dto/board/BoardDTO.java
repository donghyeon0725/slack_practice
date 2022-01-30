package com.slack.slack.common.dto.board;

import com.slack.slack.common.code.Status;
import com.slack.slack.common.entity.Board;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class BoardDTO {

    private Integer boardId;

    private String name;

    private String content;

    private String bannerPath;

    private Status status;

    private Date date;

    public BoardDTO(Board board) {
        BeanUtils.copyProperties(board, this);
    }

    public boolean isBannerEmpty() {
        return this.bannerPath == null || this.bannerPath.replaceAll(" ", "").equals("");
    }

    public BoardDTO changeStatus(Status status) {
        if (status == null)
            return this;

        this.status = status;
        return this;
    }
}
