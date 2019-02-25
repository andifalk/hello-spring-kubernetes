package com.example.hello;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/")
public class HelloController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloController.class);
    
    private final String defaultMessage;

    private final String defaultPrefix;

    public HelloController(@Autowired MessageConfig messageConfig) {
        this.defaultPrefix = messageConfig.getPrefix();
        this.defaultMessage = messageConfig.getMessage();  
        LOGGER.info("prefix '{}'", defaultPrefix);      
        LOGGER.info("message '{}'", defaultMessage);      
    }

    @GetMapping
    public Message hello(@RequestParam(required=false) String message) {
        return new Message(defaultPrefix + " " + (message != null ? message : defaultMessage));
    }

}