package al.silvio.warehouse.model;

import al.silvio.warehouse.auth.model.user.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
public class OrderSpecification implements Specification<Order> {
    private OrderStatus status;
    private String userName;
    
    @Override
    public Predicate toPredicate(@NonNull Root<Order> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        if (status != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), status));
        }
        if (!StringUtils.isBlank(userName)) {
            predicates.add(criteriaBuilder.equal(root.get("customer").<User>get("email"), userName));
        }
        query.orderBy(criteriaBuilder.desc(root.get("submittedDate")));
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}