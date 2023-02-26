package al.silvio.warehouse.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.mail")
@Getter
@Setter
public class MailProperties {
    private String username;
    private String password;
    private String host;
    private int port;
    private String from;
    
}
