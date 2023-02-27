package al.silvio.warehouse.manager;

import al.silvio.warehouse.model.Truck;
import al.silvio.warehouse.model.TruckTier;
import al.silvio.warehouse.repository.TruckRepository;
import al.silvio.warehouse.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TruckManager {
    
    private final TruckRepository localRepository;
    
    public List<Truck> getTruckList(LocalDate shippingDate, TruckTier truckTier) {
        if(truckTier != null && shippingDate != null) {
            return localRepository.findAllAvailableTrucks(shippingDate, truckTier);
        }
        return localRepository.findAll();
    }
    
    public void createTruck(Truck truck) {
        checkChassisNumber(truck.getChassisNumber());
        checkLicencePlate(truck.getLicencePlate());
        localRepository.save(truck);
    }
    
    private void checkChassisNumber(String chassisNumber) {
        Optional<Truck> existing = localRepository.findByChassisNumber(chassisNumber);
        if (existing.isPresent()) {
            throw new CustomException(
                    "There is an existing truck registered under this chassis number: " + chassisNumber);
        }
    }
    
    private void checkLicencePlate(String licencePlate) {
        Optional<Truck> existing = localRepository.findByLicencePlate(licencePlate);
        if (existing.isPresent()) {
            throw new CustomException(
                    "This licence plate is assigned to another truck with chassis number: " + existing.get()
                            .getChassisNumber());
        }
    }
    
    public void updateTruck(Truck truck) {
        Truck existing = getByChassisNumber(truck.getChassisNumber());
        existing.setLicencePlate(truck.getLicencePlate());
        existing.setContainerVolume(truck.getContainerVolume());
        localRepository.saveAndFlush(existing);
    }
    
    public Truck getByChassisNumber(String chassisNumber) {
        return localRepository.findByChassisNumber(chassisNumber).orElseThrow(
                () -> new CustomException("There is no existing truck with chassis number: " + chassisNumber, 404));
    }
    
    public void deleteTruck(String chassisNumber) {
        Truck existing = getByChassisNumber(chassisNumber);
        localRepository.delete(existing);
    }
}
