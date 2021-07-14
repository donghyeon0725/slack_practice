package com.slack.slack.domain.card;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.slack.slack.domain.team.TeamActivity;
import com.slack.slack.domain.team.TeamActivityReturnDTO;
import com.slack.slack.system.State;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AttachmentReturnDTO {
    private Integer id;

    private String path;

    private String systemFilename;

    private String extension;

    private String filename;

    private Long size;

    private String description;

    private Date date;

    private State state;
}
