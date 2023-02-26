package al.silvio.warehouse.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderFilterParam {
    private OrderStatus status;
    private String userName;
}
