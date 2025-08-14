package com.FoZ.guessIt.Services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    private final JavaMailSender mailSender;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationCode(String to, String code) throws RuntimeException {
        String subject = "Your Verification Code";
        String content = "<html>" +
                "<body>" +
                "<h2 style='color: #4CAF50;'>GUESS IT VERIFICATION CODE</h2>" +
                "<p>Hello,</p>" +
                "<p>Your verification code is:</p>" +
                "<h1 style='color: #2196F3;'>" + code + "</h1>" +
                "<p>Please enter this code to verify your account.</p>" +
                "<br/>" +
                "<p style='font-size: 12px; color: gray;'>If you did not request this, please ignore this email.</p>" +
                "</body>" +
                "</html>";

        sendHtmlEmail(to, subject, content);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom("noreply@gmail.com");

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send HTML email", e);
        }
    }
}

