package com.example.liga_exam.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name="orders")
public class Order {

    //@todo: декомпозиция!

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToMany
    @JoinTable(
            name = "order_service",
            joinColumns = @JoinColumn(name="order_id"),
            inverseJoinColumns = @JoinColumn(name ="service_id")
    )
    @Fetch(FetchMode.SUBSELECT)
    private Set<Service> services;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @NotNull
    private User user;
    @Column(columnDefinition = "boolean default false")
    private boolean canceled;
    @OneToOne
    @JoinColumn(name = "reservation_id",referencedColumnName = "id")
    @NotNull
    private Reservation reservation;
}
