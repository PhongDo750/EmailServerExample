package com.example.test.mail.controller;

import com.example.test.mail.dto.ApiResponse;
import com.example.test.mail.dto.SenderEmail;
import com.example.test.mail.service.SMTPService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/senders")
public class SMTPController {
    private final SMTPService smtpService;

    @PostMapping(value = "", consumes = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ApiResponse<?> sendEmail(@RequestPart("email") @Valid String email,
                                    @RequestPart(value = "files", required = false) List<MultipartFile> files ) throws IOException, MessagingException {
        SenderEmail senderEmail;
        ObjectMapper objectMapper = new ObjectMapper();
        senderEmail = objectMapper.readValue(email, SenderEmail.class);
        smtpService.sendMail(senderEmail, files);
        return ApiResponse.builder()
                .message("Email sent")
                .build();
    }
}
