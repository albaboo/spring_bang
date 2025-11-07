package com.exemple.bang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemple.bang.repository.WeaponCardRepository;

@Service
public class WeaponCardService {

    @Autowired
    private WeaponCardRepository repository;

}
