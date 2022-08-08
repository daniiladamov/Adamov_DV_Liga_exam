package com.example.liga_exam.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar(255) default 'ACTIVE'")
    private OrderStatus orderStatus;

    @NotNull
    @Column(name="cost")
    private BigDecimal cost;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.REMOVE,CascadeType.REFRESH})
    @JoinTable(
            name = "order_operation",
            joinColumns = @JoinColumn(name="order_id"),
            inverseJoinColumns = @JoinColumn(name ="operation_id")
    )
    @Fetch(value=FetchMode.SUBSELECT)
    private Set<Operation> operations;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "box_id", referencedColumnName = "id")
    @NotNull
    private Box box;
}
