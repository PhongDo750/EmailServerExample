package com.example.test.mail.server;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

@Component
public class GreenMailServer {
    @PostConstruct
    public void start() {
        ServerSetup smtps = ServerSetup.SMTPS;
        ServerSetup imap = ServerSetup.IMAP;
        ServerSetup pop3 = ServerSetup.POP3;

        GreenMail greenMail = new GreenMail(new ServerSetup[]{smtps, pop3});
        greenMail.start();
        greenMail.setUser("sender@localhost", "sender", "password");
        greenMail.setUser("recipient@localhost", "recipient", "password");

        System.out.println("portSMPTS : " + smtps.getPort());
//        System.out.println("imaps : " + imap.getPort());
        System.out.println("pop3s : " + pop3.getPort());
    }
}
