package al.silvio.warehouse.model.ui;

import al.silvio.warehouse.auth.model.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderAuthority {
    private Long userId;
    private String email;
    private UserRole type;
}
