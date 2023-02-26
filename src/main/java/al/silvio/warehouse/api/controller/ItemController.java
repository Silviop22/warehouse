package al.silvio.warehouse.api.controller;

import al.silvio.warehouse.api.service.ItemService;
import al.silvio.warehouse.auth.model.user.UserRole;
import al.silvio.warehouse.auth.service.TokenService;
import al.silvio.warehouse.model.ui.ItemDto;
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
@RequestMapping("/warehouse/api/v1/item")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;
    private final TokenService tokenService;
    
    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(
            @PathVariable
            Long id) {
        try {
            return ResponseEntity.ok(service.getById(id));
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad request. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<Object> getItemList() {
        try {
            return ResponseEntity.ok(service.getList());
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad request. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend.", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping
    public ResponseEntity<Object> createItem(
            @RequestHeader("Authorization")
            String token,
            @RequestBody
            ItemDto request) {
        try {
            tokenService.authorizeOperation(token, List.of(UserRole.WAREHOUSE_MANAGER));
            Long id = service.createItem(request);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", "/item/" + id);
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
    public ResponseEntity<Object> updateItem(
            @RequestHeader("Authorization")
            String token,
            @PathVariable
            Long id,
            @RequestBody
            ItemDto request) {
        try {
            tokenService.authorizeOperation(token, List.of(UserRole.WAREHOUSE_MANAGER));
            service.updateItem(request, id);
            return ResponseEntity.ok("Item updated successfully");
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad Request. Could not update resource. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(
            @RequestHeader("Authorization")
            String token,
            @PathVariable
            Long id) {
        try {
            tokenService.authorizeOperation(token, List.of(UserRole.WAREHOUSE_MANAGER));
            service.deleteItem(id);
            return new ResponseEntity<>("Item deleted successfully", HttpStatus.OK);
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
