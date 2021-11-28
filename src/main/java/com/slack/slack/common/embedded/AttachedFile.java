package com.slack.slack.common.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.File;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachedFile {
    private String path;

    private String systemFilename;

    private String extension;

    private String filename;

    private Long size;

    public String absolutePath() {
        return this.path + File.separator + this.systemFilename;
    }
}
