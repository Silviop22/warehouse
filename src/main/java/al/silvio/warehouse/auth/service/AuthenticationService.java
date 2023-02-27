package al.silvio.warehouse.auth.service;

import al.silvio.warehouse.auth.manager.TokenManager;
import al.silvio.warehouse.auth.model.token.Token;
import al.silvio.warehouse.auth.model.ui.AuthenticationRequest;
import al.silvio.warehouse.auth.model.ui.AuthenticationResponse;
import al.silvio.warehouse.auth.model.ui.UserDto;
import al.silvio.warehouse.auth.model.user.User;
import al.silvio.warehouse.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final TokenManager tokenManager;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    public AuthenticationResponse register(UserDto request) {
        User user = userService.registerCustomer(request);
        var jwtToken = jwtService.generateToken(user);
        tokenManager.storeToken(user, jwtToken);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
    
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        UsernamePasswordAuthenticationToken usr = new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getPassword());
        authenticationManager.authenticate(usr);
        User user = userService.getExistingUser(request.getEmail());
        String jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        tokenManager.storeToken(user, jwtToken);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }
    
    private void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenManager.getUserTokens(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.stream().map(Token::getToken).forEach(tokenManager::invalidateToken);
    }
    
    public void renewPassword(String email) {
        userService.renewPassword(email);
    }
    
    public String getRole() {
        return getPrincipal().getAuthorities().stream()
                .findAny()
                .orElseThrow(() -> new CustomException("Something went wrong", 400))
                .getAuthority();
    }
    public UserDetails getPrincipal() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

