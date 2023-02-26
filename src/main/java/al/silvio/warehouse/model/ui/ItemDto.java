package al.silvio.warehouse.model.ui;

import lombok.Data;

@Data
public class ItemDto {
    Long id;
    String name;
    Double quantity;
    Double unitPrice;
    Double packageVolume;
}
