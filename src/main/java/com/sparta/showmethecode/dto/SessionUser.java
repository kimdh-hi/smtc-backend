package com.sparta.showmethecode.dto;

import com.sparta.showmethecode.domain.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;

    public SessionUser(User user){
        this.name = user.getNickname();
        this.email = user.getUsername();
    }
}
