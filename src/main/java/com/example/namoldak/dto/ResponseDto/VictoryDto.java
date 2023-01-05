package com.example.namoldak.dto.ResponseDto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class VictoryDto {
    List<String> winner = new ArrayList<>();
    List<String> loser = new ArrayList<>();

    public void setWinner(String winner){
        this.winner.add(winner);
    }
    public void setLoser(String loser){
        this.loser.add(loser);
    }
}
