package com.example.liga_exam.specification;

import com.example.liga_exam.entity.Box;
import com.example.liga_exam.entity.Order;
import com.example.liga_exam.dto.request.OrderSearch;
import com.example.liga_exam.entity.Order_;
import com.example.liga_exam.entity.User;
import com.example.liga_exam.service.BoxService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class OrderSpecification implements Specification<Order> {
    private final OrderSearch orderSearch;
    private final BoxService boxService;

    @Override
    public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
         List<Predicate> predicates = new ArrayList<>();
         if (Objects.nonNull(orderSearch.getBoxId())){
             Box box=boxService.getBox(orderSearch.getBoxId());
             Predicate equal = criteriaBuilder.equal(root.get(Order_.BOX), box);
             predicates.add(equal);
         }
         if (Objects.nonNull(orderSearch.getDate())){
             Predicate dateEqual=criteriaBuilder.equal(
                     root.get(Order_.date),orderSearch.getDate());
             predicates.add(dateEqual);
         }
         if (Objects.nonNull(orderSearch.getStart())){
             Predicate startEqual=criteriaBuilder.greaterThanOrEqualTo(root.get(Order_.startTime),
                     orderSearch.getStart());
             predicates.add(startEqual);
         }
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }

    public static Specification<Order> revenuePredicate(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            Predicate predicate=criteriaBuilder.equal(root.get(Order_.done),true);
            if (Objects.nonNull(fromDate))
                predicate= criteriaBuilder.and(predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get(Order_.date),fromDate));
            if (Objects.nonNull(toDate))
                    predicate=criteriaBuilder.and(predicate,
                            criteriaBuilder.lessThanOrEqualTo(root.get(Order_.date),toDate));
            return predicate;
        };
    }

    public static Specification<Order> userActiveOrders(User user){
        return (root, query, criteriaBuilder) -> {
            Predicate equalUser = criteriaBuilder.equal(root.get(Order_.user), user);
            Predicate isActive=criteriaBuilder.equal(root.get(Order_.active),true);
            return criteriaBuilder.and(equalUser,isActive);
        };
    }
}
