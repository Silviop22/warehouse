package al.silvio.warehouse.auth.manager;

import al.silvio.warehouse.auth.model.token.Token;
import al.silvio.warehouse.auth.model.token.TokenType;
import al.silvio.warehouse.auth.model.user.User;
import al.silvio.warehouse.auth.repository.TokenRepository;
import al.silvio.warehouse.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TokenManager {
    private final TokenRepository tokenRepository;
    
    public List<Token> getUserTokens(Long userId) {
        return tokenRepository.findAllValidTokenByUser(userId);
    }
    
    public void storeToken(User user, String jwtToken) {
        Token token = Token.builder().user(user).token(jwtToken).tokenType(TokenType.BEARER).expired(false)
                .revoked(false).build();
        tokenRepository.save(token);
    }
    
    public boolean isValidToken(String jwt) {
        return getByToken(jwt).map(t -> !t.isExpired() && !t.isRevoked()).orElse(false);
    }
    
    public Optional<Token> getByToken(String jwt) {
        return tokenRepository.findByToken(jwt);
    }
    
    public void invalidateToken(String jwt) {
        Optional<Token> existing = getByToken(jwt);
        
        if (existing.isEmpty()) {
            throw new CustomException("The token you are trying to revoke is not present");
        }
        
        existing.get().setExpired(true);
        existing.get().setRevoked(true);
        tokenRepository.save(existing.get());
    }
}
