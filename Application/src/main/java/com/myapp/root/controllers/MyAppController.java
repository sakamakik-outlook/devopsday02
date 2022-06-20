package com.myapp.root.controllers;

import com.myapp.root.servicebus.AzureService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyAppController {
    
    @RequestMapping("/")
    public String index() {
        return "index.html";
    }

    @ResponseBody
    @RequestMapping("/send")
    public String send() {
        return AzureService.sendMessages();
    }

    @ResponseBody
    @RequestMapping("/receive")
    public String receive() {
        return AzureService.receiveMessages();
    }


}
