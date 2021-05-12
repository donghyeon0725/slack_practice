package com.slack.slack.domain.user;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 임시 유저 저장소
 * */
@Component
public class UserRepository {
    private static List<User> list = new ArrayList<>();
    static {
        list.add(new User(1, "ehdgus@naver.com","1234", "김동현","OK", new Date()));
    }

    public User findByEmail(String email) {
        for (User user : list) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        return null;
    }

    public List<User> findAllUser() {
        return list;
    }
}
