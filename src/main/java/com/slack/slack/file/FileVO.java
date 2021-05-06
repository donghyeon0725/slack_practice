package com.slack.slack.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FileVO {
    private String fileName;
    private String systemName;
    private String ext;
    private Long fileSize;
    private String path;
    private String absolutePath;
}
