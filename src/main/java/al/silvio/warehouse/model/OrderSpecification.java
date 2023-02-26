package al.silvio.warehouse.model;

import al.silvio.warehouse.auth.model.user.User;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {
    
    public static Specification<Order> getFilteredOrders(OrderFilterParam params) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (params.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), params.getStatus()));
            }
            if (params.getUserName() != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer").<User>get("email"), params.getUserName()));
            }
            criteriaQuery.orderBy(criteriaBuilder.desc(root.get("submittedDate")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}