package com.tomi.jwtsecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping
    public String noAuth() {
        return "No Auth";
    }

    @GetMapping("/require-user")
    public String requireUserRole() {
        return "Require User";
    }

    @GetMapping("/require-admin")
    public String requireManagerRole() {
        return "Require Admin";
    }

}
