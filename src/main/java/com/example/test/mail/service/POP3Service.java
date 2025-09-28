package com.example.test.mail.service;

import com.example.test.mail.helper.Helper;
import jakarta.mail.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Slf4j
public class POP3Service {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port.pop3}")
    private String pop3Port;

    public void readInbox(String username, String password) throws Exception {
        // Cấu hình properties cho POP3
        Properties props = new Properties();
        props.put("mail.store.protocol", "pop3");
        props.put("mail.pop3.host", host);
        props.put("mail.pop3.port", pop3Port);
        props.put("mail.pop3.ssl.trust", "*");
        props.put("mail.pop3.starttls.enable", "true");

        // Tạo session
        Session session = Session.getInstance(props);
        session.setDebug(true);

        // Lấy store (POP3)
        Store store = session.getStore("pop3");
        store.connect(host, Integer.parseInt(pop3Port), username, password);

        // POP3 chỉ có Inbox
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);

        Message[] messages = inbox.getMessages();
        System.out.println("📩 Total messages: " + messages.length);

        for (Message message : messages) {
            System.out.println("---------------------------------");
            log.info("---------------------------------");
            log.info("📨 From: {}", message.getFrom()[0]);
            log.info("📝 Subject: {}", message.getSubject());
            log.info("📄 Content: {}", Helper.getTextFromMessage(message));

            Helper.saveMessageContent(message, "G:\\testEmail");
        }

        inbox.close(false);
        store.close();
    }
}
