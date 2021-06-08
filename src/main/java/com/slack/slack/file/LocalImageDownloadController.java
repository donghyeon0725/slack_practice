package com.slack.slack.file;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

@Controller
public class LocalImageDownloadController {

    /**
     * 임시로 이미지를 불러 올 수 있도록 컨트롤러 생성
     * path의 경로 구분자는 모두 @으로 치환해서 요청해야함
     * */
    @GetMapping("/getImage/{path}")
    public void getImage(@PathVariable String path, HttpServletResponse res) throws Exception {

        String fileNm = path.split("@")[path.split("@").length-1];
        String ext = fileNm.split("\\.")[fileNm.split("\\.").length-1];
        FileInputStream in= null;
        BufferedInputStream fin = null;
        BufferedOutputStream out = null;

        /* 윈도우에서만 사용이 가능하기 때문에, 다른 운영체제를 사용한다면 replacement를 적절한 대체재로 바꾸어 주어야 합니다. */
        path = path.replaceAll("@", "/");

        try {
            res.setContentType("image/" + ext);
            res.setHeader("Content-Disposition", "inline;filename=" + fileNm);
            File file = new File(path);

            System.out.println(file.exists());
            if(file.exists()){
                in = new FileInputStream(file);
                fin = new BufferedInputStream(in);
                out = new BufferedOutputStream(res.getOutputStream());
                int len;
                byte[] buf = new byte[1024];
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        } finally {
            if(out != null){ out.flush(); }
            if(out != null){ out.close(); }
            if(in != null){ in.close(); }
            if(fin != null) { fin.close(); }
        }
    }
}
