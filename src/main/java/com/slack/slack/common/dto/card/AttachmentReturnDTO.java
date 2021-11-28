package com.slack.slack.common.dto.card;

import com.slack.slack.common.code.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
