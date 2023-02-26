package al.silvio.warehouse.auth.service;

import al.silvio.warehouse.auth.manager.UserManager;
import al.silvio.warehouse.auth.model.ui.UserDto;
import al.silvio.warehouse.auth.model.user.User;
import al.silvio.warehouse.auth.model.user.UserRole;
import al.silvio.warehouse.utils.PasswordUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserManager manager;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordUtils passwordUtils;
    
    public UserDto getById(Long id) {
        User user = manager.getById(id);
        return UserDto.builder().id(user.getId()).email(user.getEmail()).firstname(user.getFirstName())
                .lastname(user.getLastName()).build();
    }
    
    public List<UserDto> getList() {
        return manager.getUserList().stream()
                .map(user -> UserDto.builder().id(user.getId()).email(user.getEmail()).firstname(user.getFirstName())
                        .lastname(user.getLastName()).role(user.getRole()).build()).toList();
    }
    
    public User registerCustomer(UserDto userDto) {
        userDto.setRole(UserRole.CLIENT);
        return doCreate(userDto);
    }
    
    private User doCreate(UserDto userDto) {
        User user = User.builder().email(userDto.getEmail()).firstName(userDto.getFirstname())
                .lastName(userDto.getLastname()).password(passwordEncoder.encode(userDto.getPassword()))
                .role(userDto.getRole()).enabled(true).expired(false).credentialsExpired(false).locked(false).build();
        return manager.createUser(user);
    }
    
    public Long createUser(UserDto userDto) {
        return doCreate(userDto).getId();
    }
    
    public void updateUser(UserDto userDto, Long id) {
        User updateCandidate = User.builder().id(id).email(userDto.getEmail()).firstName(userDto.getFirstname())
                .lastName(userDto.getLastname()).password(userDto.getPassword()).role(userDto.getRole()).enabled(true)
                .expired(false).credentialsExpired(false).locked(false).build();
        manager.updateUser(updateCandidate);
    }
    
    public void deleteUser(Long id) {
        manager.deleteUser(id);
    }
    
    public void renewPassword(String email) {
        User existing = getExistingUser(email);
        String password = passwordUtils.getRandomPassword();
        existing.setPassword(passwordEncoder.encode(password));
        manager.updateUser(existing);
        emailService.sendMail(email, password);
    }
    
    public User getExistingUser(String email) {
        return manager.getByUserName(email);
    }
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getExistingUser(username);
    }
}
