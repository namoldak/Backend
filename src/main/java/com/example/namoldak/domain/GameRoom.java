package com.example.namoldak.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.List;

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

    @JsonIgnore
    @JoinColumn(name = "gameroommember_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private GameRoomMember gameRoomMember;

    @Column(nullable = false)
    private String owner;

    @Column(nullable = false)
    private String status;

    public void setOwner(String owner){
        this.owner = owner;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

