package al.silvio.warehouse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "trucks",
        uniqueConstraints = @UniqueConstraint(columnNames = { "chassis_number", "container_volume" }))
@Getter
@Setter
public class Truck {
    @Id
    @Column(name = "chassis_number")
    String chassisNumber;
    
    @Column(name = "licence_plate")
    String licencePlate;
    
    @Column(name = "container_volume")
    Double containerVolume;
    
}
