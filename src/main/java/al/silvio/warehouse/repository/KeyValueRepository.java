package al.silvio.warehouse.repository;

import al.silvio.warehouse.model.KeyValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyValueRepository extends JpaRepository<KeyValue, String> {
}
