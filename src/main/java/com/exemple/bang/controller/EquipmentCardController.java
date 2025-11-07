package com.exemple.bang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemple.bang.service.EquipmentCardService;

@RestController
@RequestMapping("/api/equipment-card")
public class EquipmentCardController {

    @Autowired
    private EquipmentCardService service;

}
