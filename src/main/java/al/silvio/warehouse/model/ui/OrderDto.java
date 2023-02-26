package al.silvio.warehouse.model.ui;

import al.silvio.warehouse.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class OrderDto {
    private Long orderNumber;
    private LocalDate submittedDate;
    private OrderStatus status;
}
