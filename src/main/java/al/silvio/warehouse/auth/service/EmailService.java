package al.silvio.warehouse.auth.service;

import al.silvio.warehouse.auth.config.MailProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class EmailService {
    private static final MailProperties mailProperties = new MailProperties();
    private final JavaMailSender mailSender;
    
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Async
    public void sendMail(String recipient, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailProperties.getFrom());
            message.setTo(recipient);
            message.setSubject("Your new password for Warehouse");
            message.setText("Please find enclosed your temporary password: " + password);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Could not send email to {}", recipient, e);
        }
    }
}
