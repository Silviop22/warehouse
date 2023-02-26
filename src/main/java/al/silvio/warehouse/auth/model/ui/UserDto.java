package al.silvio.warehouse.auth.model.ui;

import al.silvio.warehouse.auth.model.user.UserRole;
import al.silvio.warehouse.auth.model.validation.CreateUserValidation;
import al.silvio.warehouse.auth.model.validation.UpdateUserValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @Null(groups = CreateUserValidation.class)
    private Long id;
    @NotNull(groups = { CreateUserValidation.class, UpdateUserValidation.class })
    private String firstname;
    @NotNull(groups = { CreateUserValidation.class, UpdateUserValidation.class })
    private String lastname;
    @NotNull(groups = { CreateUserValidation.class, UpdateUserValidation.class })
    private String email;
    @NotNull(groups = { CreateUserValidation.class, UpdateUserValidation.class })
    private String password;
    @NotNull(groups = { CreateUserValidation.class, UpdateUserValidation.class })
    private UserRole role;
}