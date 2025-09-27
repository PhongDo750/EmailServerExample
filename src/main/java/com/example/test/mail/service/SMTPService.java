package com.example.test.mail.service;

import com.example.test.mail.dto.SenderEmail;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Service
public class SMTPService {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port.smtps}")
    private int port;

    public void sendMail(SenderEmail senderEmail, List<MultipartFile> files) throws MessagingException, IOException {
        // Config SMTP
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtps");   // 👈 đổi sang smtps
        props.put("mail.smtps.host", host);
        props.put("mail.smtps.port", port);

        props.put("mail.smtps.auth", "true");
        props.put("mail.smtps.ssl.enable", "true");      // bật implicit TLS
        props.put("mail.smtps.ssl.trust", "*");
        props.put("mail.smtps.ssl.checkserveridentity", "false");

        // Tạo session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail.getUsername(), senderEmail.getPassword());
            }
        });
        session.setDebug(true);

        // Tạo message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail.getFrom()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(senderEmail.getTo()));
        message.setSubject(senderEmail.getSubject(), "UTF-8");

        // Body + attachments
        Multipart multipart = new MimeMultipart();

        // Nội dung chính
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(senderEmail.getBody(), "UTF-8");
        multipart.addBodyPart(textPart);

        // File đính kèm
        if (files != null) {
            for (MultipartFile file : files) {
                MimeBodyPart attachment = new MimeBodyPart();
                DataSource dataSource = new ByteArrayDataSource(file.getBytes(), file.getContentType());
                attachment.setDataHandler(new DataHandler(dataSource));
                attachment.setFileName(file.getOriginalFilename());

                multipart.addBodyPart(attachment);
            }
        }

        message.setContent(multipart);

        // Gửi mail
        Transport transport = session.getTransport("smtps");
        transport.connect(host, port, senderEmail.getUsername(), senderEmail.getPassword());
        transport.sendMessage(message, message.getAllRecipients());
        System.out.println("✅ Email sent successfully!");
    }
}


