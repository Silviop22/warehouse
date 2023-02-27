package al.silvio.warehouse.api.service;

import al.silvio.warehouse.config.KeyValueObjectMapper;
import al.silvio.warehouse.model.Key;
import al.silvio.warehouse.model.KeyValue;
import al.silvio.warehouse.repository.KeyValueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class KeyValueService {
    
    private final KeyValueRepository keyValueRepository;
    @KeyValueObjectMapper
    private final ObjectMapper objectMapper;
    
    public <T> Optional<T> find(Key<T> key) {
        return keyValueRepository.findById(key.getKey())
                .map(KeyValue::getValue)
                .filter(StringUtils::isNotBlank)
                .map(value -> readJsonValue(key, value));
    }
    
    public <T> T findOrException(Key<T> key) {
        return find(key).orElseThrow(() -> new NoSuchElementException("Key not found: " + key.getKey()));
    }
    
    private <T> T readJsonValue(Key<T> key, String value) {
        try {
            return objectMapper.readValue(value, key.getValueClass());
        } catch (IOException e) {
            throw new IllegalStateException("Invalid setting: " + key.getKey(), e);
        }
    }
}