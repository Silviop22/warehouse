package al.silvio.warehouse.auth.controller;

import al.silvio.warehouse.auth.model.ui.AuthenticationRequest;
import al.silvio.warehouse.auth.model.ui.UserDto;
import al.silvio.warehouse.auth.service.AuthenticationService;
import al.silvio.warehouse.utils.AppUtils;
import al.silvio.warehouse.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/warehouse/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    
    private final AuthenticationService service;
    
    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @RequestBody
            UserDto request) {
        try {
            return ResponseEntity.ok(service.register(request));
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad request. Registration failed: {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Object> authenticate(
            @RequestBody
            AuthenticationRequest request) {
        try {
            return ResponseEntity.ok(service.authenticate(request));
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Authentication failed: {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping("/password/renew")
    public ResponseEntity<Object> renewPassword(
            @RequestParam
            String email) {
        try {
            service.renewPassword(email);
            return ResponseEntity.ok("A new password has been sent to your mail inbox.");
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Failed password renewal : {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

