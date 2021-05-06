package com.slack.slack.test;

import com.slack.slack.file.FileManager;
import com.slack.slack.file.FileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 파일 업로드 테스트를 위한 컨트롤러
 * */
@Controller
public class ViewController {

    @Autowired
    private FileManager fileManager;



    @GetMapping("file")
    public String file_test() {
        return "file";
    }

    @PostMapping("file")
    public String file_upload(HttpServletRequest request) {
        List<FileVO> list = fileManager.fileUpload(request);

        for (FileVO file : list) {
            System.out.println(file.getPath());
            System.out.println(file.getFileName());
            System.out.println(file.getExt());
            System.out.println(file.getSystemName());
        }

        boolean result = fileManager.deleteFile(list);
        System.out.println("result : " + result);

        return "file";
    }
}
