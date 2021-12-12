package com.slack.slack.common.code;

import com.slack.slack.common.exception.InvalidInputException;
import lombok.AllArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public enum RegularExpression {
    PW_ALPHA_NUM_SPE("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,20}$"), // 영문, 숫자, 특수문자
    PW_ALPHA_NUM("^[A-Za-z[0-9]]{10,20}$"), // 영문, 숫자
    PW_ALPHA_SPE("^[[0-9]$@$!%*#?&]{10,20}$"), //영문,  특수문자
    PW_SPE_NUM("^[[A-Za-z]$@$!%*#?&]{10,20}$"), // 특수문자, 숫자
    PW_SAME("(\\w)\\1\\1\\1"); // 같은 문자, 숫자

    private String exp;

    public boolean isValidate(String string) {
        if (!Pattern.compile(this.exp).matcher(string).find())
            return false;
        return true;
    }

}
