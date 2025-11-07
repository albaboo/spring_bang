package com.exemple.bang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemple.bang.service.WeaponCardService;

@RestController
@RequestMapping("/api/weapon-card")
public class WeaponCardController {

    @Autowired
    private WeaponCardService service;

}
