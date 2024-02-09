package com.crm.sms.controller;

import com.crm.sms.service.SMSService;
import com.crm.sms.dto.SMSDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
public class SMSController {

    private final SMSService smsService;
    @Autowired
    public SMSController(SMSService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public String sendSMS(@RequestBody SMSDto smsDto) {
        smsService.sendSMS(smsDto.getTo(), smsDto.getMessage());
        return "Message sent";
    }
}