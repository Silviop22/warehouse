package al.silvio.warehouse.repository;

import al.silvio.warehouse.model.Truck;
import al.silvio.warehouse.model.TruckTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface TruckRepository extends JpaRepository<Truck, Long> {
    Optional<Truck> findByChassisNumber(String chassisNumber);
    Optional<Truck> findByLicencePlate(String licencePlate);
    @Query("SELECT t from Truck t " +
            "WHERE t.chassisNumber not in (select distinct t1.chassisNumber " +
                "from Truck t1 inner join " +
                "OrderTruck ot " +
                "on t1 = ot.truck " +
                "inner join Order o " +
                "on o = ot.order " +
                "where o.deadlineDate=?1) " +
            "and t.tier=?2")
    List<Truck> findAllAvailableTrucks(LocalDate shippingDate, TruckTier tier);
}
