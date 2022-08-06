package com.example.liga_exam.specification;

import com.example.liga_exam.entity.Box;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalTime;

public class BoxSpecification {
    public static Specification<Box> freeBoxes(LocalTime time){
        return new Specification<Box>() {
            @Override
            public Predicate toPredicate(Root<Box> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
return null;
            }
        };
    }
}
