package al.silvio.warehouse.auth.config;

import al.silvio.warehouse.auth.manager.TokenManager;
import al.silvio.warehouse.auth.service.JwtService;
import al.silvio.warehouse.auth.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final UserService userService;
    private final TokenManager tokenManager;
    
    @Override
    protected void doFilterInternal(
            @NonNull
            HttpServletRequest request,
            @NonNull
            HttpServletResponse response,
            @NonNull
            FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwt);
        UserDetails userDetails = this.userService.loadUserByUsername(userEmail);
        if (jwtService.isValidToken(jwt, userDetails) && tokenManager.isValidToken(jwt)) {
            authenticate(jwt, request, userEmail);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private void authenticate(String jwt, HttpServletRequest request, String username) {
        UserDetails userDetails = this.userService.loadUserByUsername(username);
        if (jwtService.isValidToken(jwt, userDetails) && tokenManager.isValidToken(jwt)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}
