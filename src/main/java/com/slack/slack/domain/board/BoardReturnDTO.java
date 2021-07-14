package com.slack.slack.domain.board;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.card.Card;
import com.slack.slack.domain.card.CardReturnDTO;
import com.slack.slack.domain.team.*;
import com.slack.slack.system.State;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@JsonFilter("Board")
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
