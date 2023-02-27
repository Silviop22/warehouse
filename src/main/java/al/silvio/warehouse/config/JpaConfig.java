package al.silvio.warehouse.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories({
        "al.silvio.warehouse.repository",
        "al.silvio.warehouse.auth.repository"
})
@EntityScan({
        "al.silvio.warehouse.model",
        "al.silvio.warehouse.auth.model.token",
        "al.silvio.warehouse.auth.model.user"
})
public class JpaConfig {
}
