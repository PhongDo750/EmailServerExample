package com.example.test.mail.controller;

import com.example.test.mail.service.POP3Service;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/pop3/receivers")
public class POP3Controller {
    private final POP3Service pop3Service;

    @GetMapping("/inbox")
    public String readInbox(
            @RequestParam String username,
            @RequestParam String password
    ) throws Exception {
        pop3Service.readInbox(username, password);
        return "✅ Inbox read!";
    }
}
