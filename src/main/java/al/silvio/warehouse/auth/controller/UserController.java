package al.silvio.warehouse.auth.controller;

import al.silvio.warehouse.auth.model.ui.UserDto;
import al.silvio.warehouse.auth.model.user.UserRole;
import al.silvio.warehouse.auth.service.TokenService;
import al.silvio.warehouse.auth.service.UserService;
import al.silvio.warehouse.utils.AppUtils;
import al.silvio.warehouse.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/warehouse/api/v1/user")
public class UserController {
    private final UserService service;
    private final TokenService tokenService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @RequestHeader("Authorization")
            String token,
            @PathVariable
            Long id) {
        try {
            tokenService.authorizeOperation(token, List.of(UserRole.SYSTEM_ADMIN));
            return ResponseEntity.ok(service.getById(id));
        } catch (CustomException customException) {
            String errorMessage = customException.getMessage();
            log.warn(errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(customException.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<Object> getList(
            @RequestHeader("Authorization")
            String token) {
        try {
            tokenService.authorizeOperation(token, List.of(UserRole.SYSTEM_ADMIN));
            return ResponseEntity.ok(service.getList());
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad request. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping
    public ResponseEntity<Object> createUser(
            @RequestHeader("Authorization")
            String token,
            @RequestBody
            UserDto request) {
        try {
            tokenService.authorizeOperation(token, List.of(UserRole.SYSTEM_ADMIN));
            Long id = service.createUser(request);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", "/user/" + id);
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad request. Could not create resource. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @RequestHeader("Authorization")
            String token,
            @PathVariable
            Long id,
            @RequestBody
            UserDto request) {
        try {
            tokenService.authorizeOperation(token, List.of(UserRole.SYSTEM_ADMIN));
            service.updateUser(request, id);
            return ResponseEntity.ok("User updated successfully.");
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad request. Could not update resource. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(
            @RequestHeader("Authorization")
            String token,
            @PathVariable
            Long id) {
        try {
            tokenService.authorizeOperation(token, List.of(UserRole.SYSTEM_ADMIN));
            service.deleteUser(id);
            return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad Request. Could not delete resource. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
