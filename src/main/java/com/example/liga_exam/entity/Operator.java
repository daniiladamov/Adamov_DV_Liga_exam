package com.example.liga_exam.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@DiscriminatorValue("operator")
@Getter
@Setter
public class Operator extends User{
    @ManyToOne
    @JoinColumn(name = "box_id",referencedColumnName = "id")
    @NotNull
    private Box box;
}
