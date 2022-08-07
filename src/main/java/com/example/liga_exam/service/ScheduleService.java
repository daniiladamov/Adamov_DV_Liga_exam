package com.example.liga_exam.service;

import com.example.liga_exam.entity.Order;
import com.example.liga_exam.entity.Order_;
import com.example.liga_exam.repository.OrderRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final OrderRepo orderRepo;
    @Value("${check_interval}")
    private Long checkInterval;

    @Scheduled(cron = "${cron-interval}")
    @Async
    @Transactional
    public void refreshOrders() {
        List<Order> orders = orderRepo.findAll(Specification.where(
                getSpecification(LocalTime.now(), LocalDate.now())
        ));
        orders.stream().peek(order -> order.setActive(false)).forEach(ord -> orderRepo.save(ord));
    }

    private Specification<Order> getSpecification(LocalTime currentTime, LocalDate currentDate) {
        return (root, query, criteriaBuilder) -> {
            Predicate equalDate = criteriaBuilder.equal(root.get(Order_.date), currentDate);
            LocalTime timeInterval = currentTime.plusMinutes(checkInterval);
            Predicate inTime = criteriaBuilder.between(root.get(Order_.startTime), currentTime, timeInterval);
            Predicate isActive = criteriaBuilder.isTrue(root.get(Order_.active));
            List<Predicate> predicates = List.of(equalDate, inTime, isActive);
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }
}
