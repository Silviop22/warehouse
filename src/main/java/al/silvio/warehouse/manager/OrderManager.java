package al.silvio.warehouse.manager;

import al.silvio.warehouse.auth.manager.UserManager;
import al.silvio.warehouse.auth.model.user.User;
import al.silvio.warehouse.model.Order;
import al.silvio.warehouse.model.OrderStatus;
import al.silvio.warehouse.repository.OrderRepository;
import al.silvio.warehouse.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderManager {
    private final OrderRepository localRepository;
    private final UserManager userManager;
    
    public List<Order> getList(Specification<Order> specification) {
        return localRepository.findAll(specification);
    }
    
    @Transactional
    public void createOrder(Order order) {
        User customer = userManager.getById(order.getCustomer().getId());
        order.setCustomer(customer);
        localRepository.save(order);
    }
    
    @Transactional
    public void updateOrder(Order updateCandidate) {
        Order existing = getById(updateCandidate.getOrderNumber());
        OrderStatus statusUpdate = updateCandidate.getStatus();
        OrderStatus currentStatus = existing.getStatus();
        
        validateStatusUpdate(currentStatus, statusUpdate);
        existing.setStatus(statusUpdate);
        
        if (isSimpleStatusUpdate(statusUpdate)) {
            doUpdate(existing);
            return;
        }
        
        if (isCustomerAction(currentStatus, statusUpdate)) {
            existing.setOrderedItems(updateCandidate.getOrderedItems());
        }
        
        if (statusUpdate.equals(OrderStatus.UNDER_DELIVERY)) {
            existing.setDeadlineDate(updateCandidate.getDeadlineDate());
            existing.setOrderTrucks(updateCandidate.getOrderTrucks());
        }
        doUpdate(existing);
    }
    
    public Order getById(Long id) {
        return localRepository.findByOrderNumber(id)
                .orElseThrow(() -> new CustomException("There is no existing order with id: " + id, 404));
    }
    
    private void validateStatusUpdate(OrderStatus currentStatus, OrderStatus statusUpdate) {
        if (isStatusUpdateForbidden(currentStatus, statusUpdate)) {
            throw new CustomException("Invalid status.", 400);
        }
    }
    
    private boolean isSimpleStatusUpdate(OrderStatus statusUpdate) {
        return List.of(OrderStatus.APPROVED, OrderStatus.DECLINED, OrderStatus.CANCELED, OrderStatus.FULFILLED)
                .contains(statusUpdate);
    }
    
    private void doUpdate(Order updateCandidate) {
        localRepository.save(updateCandidate);
    }
    
    private boolean isCustomerAction(OrderStatus currentStatus, OrderStatus statusUpdate) {
        return List.of(OrderStatus.CREATED, OrderStatus.DECLINED).contains(currentStatus) | statusUpdate.equals(
                OrderStatus.AWAITING_APPROVAL);
    }
    
    private boolean isStatusUpdateForbidden(OrderStatus currentStatus, OrderStatus statusUpdate) {
        return switch (statusUpdate) {
            case CANCELED -> List.of(OrderStatus.FULFILLED, OrderStatus.CANCELED, OrderStatus.UNDER_DELIVERY)
                    .contains(currentStatus);
            case AWAITING_APPROVAL -> !List.of(OrderStatus.CREATED, OrderStatus.DECLINED).contains(currentStatus);
            case APPROVED, DECLINED -> !currentStatus.equals(OrderStatus.AWAITING_APPROVAL);
            case UNDER_DELIVERY -> !currentStatus.equals(OrderStatus.APPROVED);
            case FULFILLED -> !currentStatus.equals(OrderStatus.UNDER_DELIVERY);
            default -> throw new CustomException("Invalid status.", 400);
        };
    }
    
    @Transactional
    public void deleteOrder(Long id) {
        Order existing = getById(id);
        if (!isEligibleForDelete(existing.getStatus())) {
            throw new CustomException("This order cannot be deleted.", 400);
        }
        localRepository.delete(existing);
    }
    
    private boolean isEligibleForDelete(OrderStatus currentStatus) {
        return currentStatus.equals(OrderStatus.FULFILLED) || currentStatus.equals(OrderStatus.CANCELED);
    }
}
