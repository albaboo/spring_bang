package com.exemple.bang.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.exemple.bang.repository.CardRepository;

@Service
public class CardService {

    @Autowired
    private CardRepository repository;

    

}
