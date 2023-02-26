package al.silvio.warehouse.auth.manager;

import al.silvio.warehouse.auth.model.user.User;
import al.silvio.warehouse.auth.repository.UserRepository;
import al.silvio.warehouse.utils.CustomException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserManager {
    private final UserRepository repository;
    
    public User getByUserName(String username) {
        return repository.findByEmail(username).orElseThrow(() -> new CustomException("The username is incorrect"));
    }
    
    public List<User> getUserList() {
        return repository.findAll();
    }
    
    public User createUser(User user) {
        Optional<User> existing = repository.findByEmail(user.getEmail());
        if (existing.isPresent()) {
            throw new CustomException("Email already in use.");
        }
        return repository.save(user);
    }
    
    public void updateUser(@NonNull User updateCandidate) {
        User existing = getById(updateCandidate.getId());
        existing.setEmail(updateCandidate.getEmail());
        existing.setFirstName(updateCandidate.getFirstName());
        existing.setLastName(updateCandidate.getLastName());
        existing.setPassword(updateCandidate.getPassword());
        existing.setRole(updateCandidate.getRole());
        existing.setExpired(updateCandidate.isExpired());
        existing.setEnabled(updateCandidate.isEnabled());
        existing.setLocked(updateCandidate.isLocked());
        existing.setCredentialsExpired(updateCandidate.isCredentialsExpired());
        repository.save(existing);
    }
    
    public User getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new CustomException("This user does not exist.", 404));
    }
    
    public void deleteUser(Long id) {
        User existing = getById(id);
        repository.delete(existing);
    }
}
