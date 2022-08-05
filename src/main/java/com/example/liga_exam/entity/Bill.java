package com.example.liga_exam.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id",referencedColumnName = "id")
    @NotNull
    private Order order;

    @NotNull
    @Column(name="cost")
    private BigDecimal cost;

    @Column(name = "pay_date", columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime payDate;


}
