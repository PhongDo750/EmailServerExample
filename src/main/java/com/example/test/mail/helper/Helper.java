package com.example.test.mail.helper;

import jakarta.mail.*;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMultipart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Helper {
    // Parse nội dung
    public static String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("text/html")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }


    public static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
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

    private static String getBodyWithoutAttachment(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("text/html")) {
            return htmlToText(message.getContent().toString());
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);

                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                    continue;
                }

                if (bodyPart.isMimeType("text/plain")) {
                    result.append(bodyPart.getContent().toString()).append("\n");
                } else if (bodyPart.isMimeType("text/html")) {
                    result.append(htmlToText(bodyPart.getContent().toString())).append("\n");
                }
            }
            return result.toString();
        }
        return "";
    }

    private static String htmlToText(String html) throws Exception {
        javax.swing.text.html.HTMLEditorKit kit = new javax.swing.text.html.HTMLEditorKit();
        javax.swing.text.Document doc = kit.createDefaultDocument();
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);

        try (java.io.Reader reader = new java.io.StringReader(html)) {
            kit.read(reader, doc, 0);
        }

        return doc.getText(0, doc.getLength());
    }


    public static void saveMessageContent(Message message, String saveDir)
            throws Exception {

        // Tạo thư mục gốc nếu chưa có
        File baseDir = new File(saveDir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        // Tạo thư mục riêng cho email này (theo timestamp hoặc subject)
        String folderName = "email_" + System.currentTimeMillis();
        File emailDir = new File(baseDir, folderName);
        emailDir.mkdirs();

        // 1. Lưu header + body
        StringBuilder sb = new StringBuilder();
        sb.append("From: ").append(Arrays.toString(message.getFrom())).append("\n");
        sb.append("To: ").append(Arrays.toString(message.getRecipients(Message.RecipientType.TO))).append("\n");
        sb.append("Subject: ").append(message.getSubject()).append("\n");
        sb.append("Date: ").append(message.getSentDate()).append("\n\n");

        String body = getBodyWithoutAttachment(message);
        sb.append("Body:\n").append(body).append("\n");

        File headerFile = new File(emailDir, "header_body.txt");
        try (FileOutputStream fos = new FileOutputStream(headerFile)) {
            fos.write(sb.toString().getBytes());
        }
        System.out.println("Saved header+body: " + headerFile.getAbsolutePath());

        // 2. Lưu file đính kèm
        if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();

            for (int i = 0; i < multipart.getCount(); i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);

                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
                        || bodyPart.getFileName() != null) {

                    MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;
                    String fileName = mimeBodyPart.getFileName();

                    File file = new File(emailDir, fileName);

                    try (InputStream is = mimeBodyPart.getInputStream();
                         FileOutputStream fos = new FileOutputStream(file)) {
                        byte[] buf = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = is.read(buf)) != -1) {
                            fos.write(buf, 0, bytesRead);
                        }
                    }

                    System.out.println("Saved attachment: " + file.getAbsolutePath());
                }
            }
        }
    }


}
