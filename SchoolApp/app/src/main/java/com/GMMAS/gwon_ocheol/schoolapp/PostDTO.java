package com.GMMAS.gwon_ocheol.schoolapp;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kwon-ohchul on 2017. 12. 28..
 */

public class PostDTO {
    public String subject, contents, nickname, password, date;

    public void setDate(String date) {
        this.date = date;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setContent(String contents) {
        this.contents = contents.trim();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
