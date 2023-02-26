package al.silvio.warehouse.model.ui;

import al.silvio.warehouse.model.validation.CreateTruckValidationGroup;
import al.silvio.warehouse.model.validation.UpdateTruckValidationGroup;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class TruckDto {
    @NotNull(groups = CreateTruckValidationGroup.class) String chassisNumber;
    @NotNull(groups = { CreateTruckValidationGroup.class, UpdateTruckValidationGroup.class }) String licencePlate;
    @NotNull(groups = { CreateTruckValidationGroup.class, UpdateTruckValidationGroup.class }) Double containerVolume;
}
