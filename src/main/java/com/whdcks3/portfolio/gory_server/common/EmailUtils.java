package com.whdcks3.portfolio.gory_server.common;

import java.util.Date;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailUtils {

    private JavaMailSender mailSender;

    public EmailUtils(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${server.url}")
    private String serverUrl;

    public void sendVerificationEmail(String to, String token) {
        String subject = "[Gori] Email Verification for Sign-up";
        String verificationLink = serverUrl + "/api/auth/activate?token=" + token;
        String content = "<p>To complete your registration, please click the link below to verify your email address.</p>"
                + "<a href=\"" + verificationLink + "\">Email verification link</a>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.info("Fail");
            e.printStackTrace();
        }
    }

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

            simpleMailMessage.setTo(toEmail);
            simpleMailMessage.setSubject(subject);
            simpleMailMessage.setText(body);
            simpleMailMessage.setSentDate(new Date());

            mailSender.send(simpleMailMessage);

            log.info("Success");
        } catch (Exception e) {
            log.info("Fail");
            e.printStackTrace();
        }
    }
}