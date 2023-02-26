package al.silvio.warehouse.auth.service;

import al.silvio.warehouse.auth.manager.TokenManager;
import al.silvio.warehouse.auth.model.token.Token;
import al.silvio.warehouse.auth.model.ui.UserDto;
import al.silvio.warehouse.auth.model.user.User;
import al.silvio.warehouse.auth.model.user.UserRole;
import al.silvio.warehouse.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenManager tokenManager;
    
    public void authorizeOperation(String token, List<UserRole> authorizedRoles) {
        UserRole userRole = getPrincipal(token).getRole();
        if (!authorizedRoles.contains(userRole)) {
            throw new CustomException("Insufficient privileges.", 403);
        }
    }
    
    public UserDto getPrincipal(String jwt) {
        jwt = StringUtils.substringAfter(jwt, "Bearer ");
        Token token = tokenManager.getByToken(jwt).orElseThrow(() -> new CustomException("Invalid token", 401));
        User user = token.getUser();
        return UserDto.builder().id(user.getId()).email(user.getEmail()).firstname(user.getFirstName())
                .lastname(user.getLastName()).role(user.getRole()).build();
    }
}
