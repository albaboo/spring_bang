package com.exemple.bang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemple.bang.service.GameService;

@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService service;

}
