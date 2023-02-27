package al.silvio.warehouse.repository;

import al.silvio.warehouse.model.Truck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@SuppressWarnings("unused")
public interface TruckRepository extends JpaRepository<Truck, Long> {
    Optional<Truck> findByChassisNumber(String chassisNumber);
    
    Optional<Truck> findByLicencePlate(String licencePlate);
}
