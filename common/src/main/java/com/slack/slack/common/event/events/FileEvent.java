package com.slack.slack.common.event.events;

import com.slack.slack.common.file.FileVO;

import java.util.List;

/**
 * 파일 업로드 도중 에러가 발생했을 때, 파일을 처리를 위한 이벤트 클래스
 * */
public class FileEvent {
    private List<FileVO> files;

    public FileEvent(List<FileVO> files) {
        this.files = files;
    }

    public List<FileVO> getFiles() {
        return files;
    }

    public FileEvent setFiles(List<FileVO> files) {
        this.files = files;
        return this;
    }
}
