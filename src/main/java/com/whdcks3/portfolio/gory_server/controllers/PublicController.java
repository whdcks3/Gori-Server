package com.whdcks3.portfolio.gory_server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.whdcks3.portfolio.gory_server.repositories.RandomCodeRepository;
import com.whdcks3.portfolio.gory_server.service.AuthService;

@Controller
@RequestMapping(value = "/")
// @CrossOrigin(origins = {"https://~~~"})
public class PublicController {

    @Autowired
    RandomCodeRepository randomCodeRepository;

    @Autowired
    AuthService authService;

    // @GetMapping("/send")
    // public String getMethodName() {
    // System.out.println("TEST");
    // return "mail/test";
    // }

    // @PostMapping("/send")
    // public String sendEmail(@RequestParam String email) {
    // authService.sendEmail(email);
    // System.out.println("메일에 발송한 코드: " + email);
    // return email;
    // }

    // @PostMapping("/send")
    // public String redirectEmail(RandomCode email) {
    // randomCodeRepository.save(email);
    // return "redirect:/send";
    // }

}
