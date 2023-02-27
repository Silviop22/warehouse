package al.silvio.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "key_value")
public class KeyValue {
    
    @Id
    @Column(unique = true, nullable = false)
    private String key;
    private String value;
}
