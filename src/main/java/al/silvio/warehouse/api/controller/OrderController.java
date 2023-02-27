package al.silvio.warehouse.api.controller;

import al.silvio.warehouse.api.service.OrderService;
import al.silvio.warehouse.model.OrderStatus;
import al.silvio.warehouse.model.ui.OrderExtendedDto;
import al.silvio.warehouse.utils.AppUtils;
import al.silvio.warehouse.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/warehouse/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    
    @GetMapping("/{orderNumber}")
    public ResponseEntity<Object> getById(@PathVariable Long orderNumber) {
        try {
            return ResponseEntity.ok(orderService.getByOrderNumber(orderNumber));
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
    public ResponseEntity<Object> getList(@RequestParam(required = false) OrderStatus status, @RequestParam(required = false) String userName,
            @RequestParam int page, @RequestParam int size) {
        try {
            return ResponseEntity.ok(orderService.getList(status, userName, page, size));
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
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Object> createOrder(@RequestBody OrderExtendedDto request, @RequestHeader("Authorization") String token) {
        try {
            Long orderNumber = orderService.createOrder(request);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", "/order/" + orderNumber);
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
    
    @PatchMapping("/{orderNumber}")
    @PreAuthorize("hasAnyAuthority('WAREHOUSE_MANAGER', 'CLIENT')")
    public ResponseEntity<Object> updateOrder(@PathVariable Long orderNumber, @RequestBody OrderExtendedDto request) {
        try {
            request.setOrderNumber(orderNumber);
            orderService.updateOrder(request);
            return ResponseEntity.ok("Oder updated successfully");
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad request. Could not update resource. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{orderNumber}")
    @PreAuthorize("hasAnyAuthority('WAREHOUSE_MANAGER')")
    public ResponseEntity<Object> deleteUser(@PathVariable Long orderNumber) {
        try {
            orderService.deleteOrder(orderNumber);
            return new ResponseEntity<>("Order deleted successfully", HttpStatus.OK);
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad Request. Could not delete user. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
