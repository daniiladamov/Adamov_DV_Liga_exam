package com.example.liga_exam.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "boxes")
public class Box {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private double ratio;

    @Column(columnDefinition = "time default '08:00'")
    private LocalTime open;

    @Column(columnDefinition = "time default '20:00'")
    private LocalTime close;

    @OneToMany(mappedBy = "box", fetch = FetchType.LAZY)
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<Employee> employees;
}
