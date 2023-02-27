package al.silvio.warehouse.repository;

import al.silvio.warehouse.model.Item;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@SuppressWarnings("unused")
public interface ItemRepository extends JpaRepository<Item, Long> {
    @NonNull Optional<Item> findById(@NonNull Long id);
}
