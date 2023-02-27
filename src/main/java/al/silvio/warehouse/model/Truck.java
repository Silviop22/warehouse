package al.silvio.warehouse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    
    @Column
    @Enumerated(EnumType.STRING)
    TruckTier tier;
    
    @OneToMany(cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "truck")
    @JsonIgnore
    private List<OrderTruck> orderTruck;
}
