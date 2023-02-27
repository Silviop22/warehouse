package al.silvio.warehouse.utils;

import al.silvio.warehouse.api.service.KeyValueService;
import al.silvio.warehouse.auth.model.user.UserRole;
import al.silvio.warehouse.model.Key;
import al.silvio.warehouse.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderUtils {
    private final KeyValueService keyValueService;
    private static final Set<DayOfWeek> WEEKEND = EnumSet.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static final HashMap<String, List<OrderStatus>> assignedActions = new HashMap<>() {{
        put(UserRole.CLIENT.name(), List.of(OrderStatus.CREATED, OrderStatus.CANCELED, OrderStatus.AWAITING_APPROVAL));
        put(UserRole.WAREHOUSE_MANAGER.name(), List.of(OrderStatus.UNDER_DELIVERY, OrderStatus.APPROVED, OrderStatus.DECLINED));
    }};
    
    public List<LocalDate> getBusinessDates(Long period) {
        LocalDate endDate = LocalDate.now().plusDays(period);
        List<LocalDate> publicHolidays = keyValueService.findOrException(Key.PUBLIC_HOLIDAYS);
        return LocalDate.now().datesUntil(endDate)
                .filter(d -> !WEEKEND.contains(d.getDayOfWeek()))
                .filter(d -> !publicHolidays.contains(d))
                .collect(Collectors.toList());
    }
    public void validateStatusUpdate(OrderStatus currentStatus, OrderStatus statusUpdate, String role) {
        if (isStatusUpdateForbidden(currentStatus, statusUpdate) | isActionForbidden(statusUpdate, role)) {
            throw new CustomException("Invalid status.", 400);
        }
    }
    
    private boolean isActionForbidden(OrderStatus statusUpdate, String role) {
        return !assignedActions.get(role).contains(statusUpdate);
    }
    
    public boolean isSimpleStatusUpdate(OrderStatus statusUpdate) {
        return List.of(OrderStatus.APPROVED, OrderStatus.DECLINED, OrderStatus.CANCELED, OrderStatus.FULFILLED)
                .contains(statusUpdate);
    }
    
    public boolean isCustomerAction(OrderStatus currentStatus, OrderStatus statusUpdate) {
        return List.of(OrderStatus.CREATED, OrderStatus.DECLINED).contains(currentStatus) | statusUpdate.equals(
                OrderStatus.AWAITING_APPROVAL);
    }
    
    private boolean isStatusUpdateForbidden(OrderStatus currentStatus, OrderStatus statusUpdate) {
        return switch (statusUpdate) {
            case CANCELED -> List.of(OrderStatus.FULFILLED, OrderStatus.CANCELED, OrderStatus.UNDER_DELIVERY)
                    .contains(currentStatus);
            case AWAITING_APPROVAL, CREATED -> !List.of(OrderStatus.CREATED, OrderStatus.DECLINED).contains(currentStatus);
            case APPROVED, DECLINED -> !currentStatus.equals(OrderStatus.AWAITING_APPROVAL);
            case UNDER_DELIVERY -> !currentStatus.equals(OrderStatus.APPROVED);
            case FULFILLED -> !currentStatus.equals(OrderStatus.UNDER_DELIVERY);
        };
    }
}
