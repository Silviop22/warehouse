package al.silvio.warehouse.auth.repository;

import al.silvio.warehouse.auth.model.user.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@SuppressWarnings("unused")
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    @NonNull Optional<User> findById(@NonNull Long id);
}