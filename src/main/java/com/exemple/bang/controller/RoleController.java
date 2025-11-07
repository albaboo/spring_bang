package com.exemple.bang.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.exemple.bang.service.RoleService;

@RestController
@RequestMapping("/api/role")
public class RoleController {

    
    @Autowired
    private RoleService service;

}
