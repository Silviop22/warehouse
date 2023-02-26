package al.silvio.warehouse.auth.service;

import al.silvio.warehouse.auth.config.MailProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Log4j2
@Service
public class EmailService {
    
    private final String username;
    private final String password;
    private final String sender;
    private final MailProperties mailProperties;
    private final Properties prop;
    
    public EmailService(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
        prop = new Properties();
        prop.put("mail.smtp.auth", true);
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", this.mailProperties.getHost());
        prop.put("mail.smtp.port", this.mailProperties.getPort());
        prop.put("mail.smtp.ssl.trust", this.mailProperties.getHost());
        this.username = this.mailProperties.getUsername();
        this.password = this.mailProperties.getPassword();
        this.sender = this.mailProperties.getFrom();
    }
    
    @Async
    public void sendMail(String recipient, String password) {
        Session session = getSmtpSession();
        session.setDebug(true);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sender));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject("Your new password for Warehouse");
            message.setText("Please find enclosed your temporary password: " + password);
            Transport.send(message);
        } catch (Exception e) {
            log.error("Could not send email to {}", recipient, e);
        }
    }
    
    private Session getSmtpSession() {
        return Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }
}
