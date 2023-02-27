package al.silvio.warehouse.manager;

import al.silvio.warehouse.auth.manager.UserManager;
import al.silvio.warehouse.auth.model.user.User;
import al.silvio.warehouse.model.Order;
import al.silvio.warehouse.model.OrderStatus;
import al.silvio.warehouse.repository.OrderRepository;
import al.silvio.warehouse.utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderManager {
    private final OrderRepository localRepository;
    private final UserManager userManager;
    
    public Page<Order> getList(Specification<Order> specification, Pageable pageRequest) {
        return localRepository.findAll(specification, pageRequest);
    }
    
    @Transactional
    public Order createOrder(Order order) {
        User customer = userManager.getById(order.getCustomer().getId());
        order.setCustomer(customer);
        return localRepository.save(order);
    }
    
    @Transactional
    public void updateOrder(Order updateCandidate) {
        localRepository.save(updateCandidate);
    }
    
    public Order getById(Long id) {
        return localRepository.findByOrderNumber(id)
                .orElseThrow(() -> new CustomException("There is no existing order with id: " + id, 404));
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
