package al.silvio.warehouse.model.ui;
import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResult<T>(List<T> content, long total) {
    
    public static <T> PagedResult<T> fromPage(Page<T> page) {
        return new PagedResult<>(page.getContent(), page.getTotalElements());
    }
}
