package org.dopamine.controller;

import org.dopamine.service.BarTenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bartender")
public class BartenderController {
    @Value("${bartender.jacob}")
    private String jacob;
    @Value("${bartender.printerName}")
    private String printerName;
    @Value("${bartender.templatePath}")
    private String templatePath;
    @Value("${bartender.printLength}")
    private Integer printLength;

    @Autowired
    private BarTenderService barTenderService;

    @GetMapping("/bartender")
    public void bartender() throws Exception{
//        barTenderService.printBarTender(jacob,printerName,templatePath,printLength);
    }
}
