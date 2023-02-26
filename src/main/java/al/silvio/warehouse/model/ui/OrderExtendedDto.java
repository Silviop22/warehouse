package al.silvio.warehouse.model.ui;

import al.silvio.warehouse.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class OrderExtendedDto {
    private Long orderNumber;
    private LocalDate submittedDate;
    private OrderStatus status;
    private LocalDate deadlineDate;
    private List<OrderItemDto> items;
    private OrderAuthority customer;
    private List<TruckDto> trucks;
}
