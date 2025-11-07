package com.exemple.bang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemple.bang.service.UseCardService;

@RestController
@RequestMapping("/api/use-card")
public class UseCardController {

    @Autowired
    private UseCardService service;

}
