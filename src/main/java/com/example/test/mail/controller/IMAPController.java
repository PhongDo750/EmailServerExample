package com.example.test.mail.controller;

import com.example.test.mail.service.IMAPService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/receivers")
public class IMAPController {
    private final IMAPService imapService;

    @GetMapping("/inbox")
    public String readInbox(
            @RequestParam String username,
            @RequestParam String password
    ) throws Exception {
        imapService.readInbox(username, password);
        return "✅ Inbox read!";
    }
}
