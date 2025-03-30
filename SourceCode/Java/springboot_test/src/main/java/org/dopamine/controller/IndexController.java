package org.dopamine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class IndexController {
    @RequestMapping({"/index","/hello","/home"})
    public String budaqian() throws Exception{
//        String path = resourceLoader.getResource("classpath:jasper/Report_V_HZ_BuDaQian.jasper").getFile().getPath();
        return "home.html";
    }

    @PostMapping("/quit")
    public void quit() throws Exception{
        System.exit(0); // 正常退出
    }
}
