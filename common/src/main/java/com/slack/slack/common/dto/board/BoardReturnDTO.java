package com.slack.slack.common.dto.board;

import com.slack.slack.common.code.Status;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BoardReturnDTO {

    private Integer id;

    private String title;

    private String content;

    private String bannerPath;

    private Status status;

    private Date date;

    public boolean isBannerEmpty() {
        return this.bannerPath == null || this.bannerPath.replaceAll(" ", "").equals("");
    }

    public BoardReturnDTO changeStatus(Status status) {
        if (status == null)
            return this;

        this.status = status;
        return this;
    }
}
