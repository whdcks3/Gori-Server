package com.whdcks3.portfolio.gory_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.whdcks3.portfolio.gory_server.repositories.SquadRepository;
import com.whdcks3.portfolio.gory_server.service.SquadService;

@SpringBootTest
@AutoConfigureMockMvc
public class SquadControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    SquadService squadService;

    @Autowired
    SquadRepository squadRepository;
}
