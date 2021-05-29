package com.slack.slack.event;

import com.slack.slack.file.FileManager;
import com.slack.slack.file.FileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 파일 이벤트 처리기
 * */
@Component
@RequiredArgsConstructor
public class FileEventHandler {

    private final FileManager fileManager;

    /**
     * 파일을 삭제합니다.
     * */
    @EventListener
    public void handle(FileEvent event) {
        List<FileVO> files = event.getFiles();

        if (files != null)
            fileManager.deleteFile(files);
    }
}
