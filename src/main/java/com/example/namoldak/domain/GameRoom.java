package com.example.namoldak.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

// 기능 : 게임룸 Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
public class GameRoom extends Timestamped{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long gameRoomId;

    @Column(nullable = false)
    private String gameRoomName;
    @Column
    private String gameRoomPassword;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private boolean status;

    public void setOwner(String owner){
        this.owner = owner;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}

