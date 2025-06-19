package com.cballa.hello_world.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class home {

    @GetMapping("/home")
    public String Home() {
        return "Hello World";
    }
}
