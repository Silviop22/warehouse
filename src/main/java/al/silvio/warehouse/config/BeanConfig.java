package al.silvio.warehouse.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class BeanConfig {
    
    @Bean
    @KeyValueObjectMapper
    // It's common to have more than one Object Mapper / WebClient / RestTemplate Bean so as a best practice we use Qualifiers
    public ObjectMapper keyValueObjectMapper() {
        return Jackson2ObjectMapperBuilder.json()
                .featuresToDisable(
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }
}
