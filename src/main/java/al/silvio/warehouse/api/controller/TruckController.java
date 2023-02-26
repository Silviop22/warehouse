package al.silvio.warehouse.api.controller;

import al.silvio.warehouse.api.service.TruckService;
import al.silvio.warehouse.model.ui.TruckDto;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/warehouse/api/v1/truck")
@PreAuthorize("hasAnyAuthority('WAREHOUSE_MANAGER')")
public class TruckController {
    
    private final TruckService truckService;
    
    @GetMapping("/{chassisNumber}")
    public ResponseEntity<Object> getByChassis(@PathVariable String chassisNumber) {
        try {
            return ResponseEntity.ok(truckService.getTruckByChassisNumber(chassisNumber));
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Resource not found. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping
    public ResponseEntity<Object> getTruckList() {
        try {
            return ResponseEntity.ok(truckService.getTruckList());
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
    public ResponseEntity<Object> createTruck(@RequestBody TruckDto request) {
        try {
            truckService.createTruck(request);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", "/truck/" + request.getChassisNumber());
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
    
    @PatchMapping("/{chassisNumber}")
    public ResponseEntity<Object> updateTruck(@PathVariable String chassisNumber, @RequestBody TruckDto request) {
        try {
            truckService.updateTruck(request, chassisNumber);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", "/truck/" + request.getChassisNumber());
            return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
        } catch (CustomException badRequest) {
            String errorMessage = badRequest.getMessage();
            log.warn("Bad Request. Could not update resource. {}", errorMessage);
            return new ResponseEntity<>(errorMessage, HttpStatus.valueOf(badRequest.getStatusCode()));
        } catch (Exception internalError) {
            log.error("Error observed on the backend", internalError);
            return new ResponseEntity<>(AppUtils.SERVER_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @DeleteMapping("/{chassisNumber}")
    public ResponseEntity<Object> deleteTruck(@PathVariable String chassisNumber) {
        try {
            truckService.deleteTruck(chassisNumber);
            return new ResponseEntity<>("Truck deleted successfully", HttpStatus.OK);
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
