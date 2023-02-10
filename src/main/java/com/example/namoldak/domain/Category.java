package com.example.namoldak.domain;

import lombok.Getter;

// 기능 : 게임 랜덤 Category 지정
@Getter
public enum Category {

    인물, 동물, 음식, 만화, 영화, 드라마;

    public static Category getRandom(){
        return values()[(int)(Math.random()* values().length)];
    }
}
