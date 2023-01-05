package com.example.namoldak.controller;

import com.example.namoldak.service.GameRearService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class GameRearController {

    private final GameRearService gameRearService;
}
