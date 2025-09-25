package com.example.test.mail.service;

import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Service
@Slf4j
public class IMAPService {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port.imap}")
    private int imapPort;

    public void readInbox(String username, String password) throws MessagingException, IOException {
        // Cấu hình properties
        Properties props = new Properties();
        props.put("mail.store.protocol", "imap");
        props.put("mail.imap.ssl.trust", "*");
        props.put("mail.imap.host", host);
        props.put("mail.imap.port", imapPort);
        props.put("mail.imap.starttls.enable", "true");

        // Tạo session
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);

        // Lấy store (IMAP)
        Store store = session.getStore("imap");
        store.connect(host, imapPort, username, password);

        // Truy cập Inbox
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();
        System.out.println("📩 Total messages: " + messages.length);

        for (Message message : messages) {
            System.out.println("---------------------------------");
//            System.out.println("From: " + message.getFrom()[0]);
//            System.out.println("Subject: " + message.getSubject());
//            System.out.println("Content: " + getTextFromMessage(message));
            log.info("---------------------------------");
            log.info("📨 From: {}", message.getFrom()[0]);
            log.info("📝 Subject: {}", message.getSubject());
            log.info("📄 Content: {}", getTextFromMessage(message));
        }

        inbox.close(false);
        store.close();
    }

    // Hàm hỗ trợ parse nội dung (kể cả multipart/attachment)
    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                result.append("[HTML content]");
            }
        }
        return result.toString();
    }
}






