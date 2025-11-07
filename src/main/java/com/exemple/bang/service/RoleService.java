package com.exemple.bang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemple.bang.repository.RoleRepository;

@Service
public class RoleService {

    @Autowired
    private RoleRepository repository;

}
