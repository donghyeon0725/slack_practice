package com.slack.slack.domain.board;

import com.slack.slack.system.State;
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

    private State state;

    private Date date;

    public boolean isBannerEmpty() {
        return this.bannerPath == null || this.bannerPath.replaceAll(" ", "").equals("");
    }
}
