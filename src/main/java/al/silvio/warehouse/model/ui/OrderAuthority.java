package al.silvio.warehouse.model.ui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderAuthority {
    private Long userId;
    private String email;
}
