package al.silvio.warehouse.model.ui;

import al.silvio.warehouse.model.TruckTier;
import al.silvio.warehouse.model.validation.CreateTruckValidationGroup;
import al.silvio.warehouse.model.validation.UpdateTruckValidationGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TruckDto {
    @NotNull(groups = CreateTruckValidationGroup.class)
    String chassisNumber;
    @NotNull(groups = { CreateTruckValidationGroup.class, UpdateTruckValidationGroup.class })
    String licencePlate;
    Double containerVolume;
    @NotNull(groups = { CreateTruckValidationGroup.class, UpdateTruckValidationGroup.class })
    TruckTier tier;
}
