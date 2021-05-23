package com.slack.slack.system;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpression {
    public static final String pw_alpha_num_spe = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}$"; // 영문, 숫자, 특수문자
    public static final String pw_alpha_num = "^[A-Za-z[0-9]]{10,20}$"; // 영문, 숫자
    public static final String pw_alpha_spe = "^[[0-9]$@$!%*#?&]{10,20}$"; //영문,  특수문자
    public static final String pw_spe_num = "^[[A-Za-z]$@$!%*#?&]{10,20}$"; // 특수문자, 숫자
    public static final String pw_same = "(\\w)\\1\\1\\1"; // 같은 문자, 숫자

    private static Matcher match;

    /**
     * 예제 샘플 코드
     * */
    public boolean pwdRegularExpressionChk(String newPwd, String oldPwd, String userId) {
        boolean chk = false;

        match = Pattern.compile(pw_alpha_num_spe).matcher(newPwd);
        if(match.find()) {
            chk = true;
        }

        // 영문, 숫자 (10~20 자리)
        match = Pattern.compile(pw_alpha_num).matcher(newPwd);
        if(match.find()) {
            chk = true;
        }

        // 영문, 특수문자 (10~20 자리)
        match = Pattern.compile(pw_alpha_spe).matcher(newPwd);
        if(match.find()) {
            chk = true;
        }

        // 특수문자, 숫자 (10~20 자리)
        match = Pattern.compile(pw_spe_num).matcher(newPwd);
        if(match.find()) {
            chk = true;
        }


        if(chk) {
            // 같은문자 4자리
            if(samePwd(newPwd)) return false;
            // 연속문자 4자리
            if(continuousPwd(newPwd)) return false;
            // 이전 아이디 4자리
            if(newPwd.equals(oldPwd)) return false;
            // 아이디와 동일 문자 4자리
            if(sameId(newPwd, userId)) return false;
        }
        return true;
    }

    /**
     * 정규 표현식을 만족하는지
     *
     * @param regularExpression
     * @param str
     * @return boolean
     * */
    public static boolean isValid(String regularExpression, String str) {
        match = Pattern.compile(regularExpression).matcher(str);
        if (match.find()) return true;
        return false;
    }

    /**
     * 같은 문자, 숫자 4자리 체크
     * @param pwd
     * @return
     */
    public boolean samePwd(String pwd) {
        match = Pattern.compile(pw_same).matcher(pwd);

        return match.find() ? true : false;
    }

    /**
     * 연속 문자, 숫자 4자리 체크
     * @param pwd
     * @return
     */
    public boolean continuousPwd(String pwd) {
        int o = 0;
        int d = 0;
        int p = 0;
        int n = 0;
        int limit = 4;

        for(int i=0; i<pwd.length(); i++) {
            char tempVal = pwd.charAt(i);
            if(i > 0 && (p = o - tempVal) > -2 && (n = p == d ? n + 1 :0) > limit -3) {
                return true;
            }

            d = p;
            o = tempVal;
        }

        return false;
    }

    /**
     * 아이디와 동일 문자 4자리 체크
     * @param pwd
     * @param id
     * @return
     */
    public boolean sameId(String pwd, String id) {
        for(int i=0; i<pwd.length()-3; i++) {
            if(id.contains(pwd.substring(i, i+4))) {
                return true;
            }
        }

        return false;
    }









}
