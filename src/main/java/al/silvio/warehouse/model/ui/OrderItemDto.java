package al.silvio.warehouse.model.ui;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDto {
    private Long id;
    private Long orderNumber;
    private ItemDto item;
    private Long quantity;
}
