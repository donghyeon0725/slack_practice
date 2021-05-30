package com.slack.slack.file;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileVO {
    private String fileName;
    private String systemName;
    private String ext;
    private Long fileSize;
    private String path;
    private String absolutePath;
}
