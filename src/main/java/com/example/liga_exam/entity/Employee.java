package com.example.liga_exam.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Table(name = "employees")
public class Employee{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "box_id",referencedColumnName = "id")
    @NotNull
    private Box box;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    @NotNull
    private User user;


}
