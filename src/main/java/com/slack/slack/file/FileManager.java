package com.slack.slack.file;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
@Component
public class FileManager {
    @Value("${custom.tempfile}")
    private String PATH;

    private String DASH = "-";
    private String POINT = ".";

    public String getUID() {
        return UUID.randomUUID().toString().replaceAll(DASH, "");
    }

    public List<FileVO> fileUpload(HttpServletRequest request) {
        //파일이 저장될 path 설정
        List<FileVO> returnResult = new ArrayList<>();

        try { // MultipartHttpServletRequest 생성
            MultipartHttpServletRequest mhsr = (MultipartHttpServletRequest) request;
            Iterator iter = mhsr.getFileNames();

            // 디레토리가 없다면 생성
            File dir = new File(PATH);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }

            // 값이 나올때까지
            while (iter.hasNext()) {
                String fieldName = iter.next().toString();
                // 파일이름으로 request로 부터 내용물을 가져온다.
                MultipartFile mfile = mhsr.getFile(fieldName);
                // 8859_1 / 한글 깨짐 방지
                String origName = new String(mfile.getOriginalFilename().getBytes("UTF-8"), "UTF-8");

                // 파일명이 없다면 패스
                if ("".equals(origName)) continue;

                // 파일 명 변경(uuid로 암호화)
                String ext = origName.substring(origName.lastIndexOf(POINT));
                String saveFileName = getUID() + ext;
                // 파일로 저장
                File serverFile = new File(PATH + File.separator + saveFileName);
                mfile.transferTo(serverFile);


                // 필요한 파일 정보 저장
                FileVO fileVO = new FileVO();
                fileVO.setFileName(origName);
                fileVO.setSystemName(serverFile.getName());
                fileVO.setAbsolutePath(serverFile.getAbsolutePath());
                fileVO.setPath(serverFile.getParentFile().getAbsolutePath());
                fileVO.setFileSize(serverFile.length());
                fileVO.setExt(ext);

                returnResult.add(fileVO);
            }

        } catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (IllegalStateException e) { // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) { // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return returnResult;
    }

    public boolean deleteFile(List<FileVO> fileList) {
        if (fileList == null)
            return false;

        Iterator iter = fileList.iterator();

        boolean result = true;

        while (iter.hasNext()) {
            FileVO file = (FileVO)iter.next();
            File serverFile = new File(file.getAbsolutePath());

            // 존재하면
            if( serverFile.exists() ) {
                // 하나라도 삭제되지 않으면
                if (!serverFile.delete()) {
                    result = false;
                }

            } else {
                log.debug("파일이 존재하지 않습니다.");
                result = false;
            }
        }

        return result;
    }
}
