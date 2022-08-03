package com.example.liga_exam.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Time;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Basic
    @DateTimeFormat(pattern = "HH:mm")
    private Time beginning;
    @Basic
    @DateTimeFormat(pattern = "HH:mm")
    private Time endTime;
    @Column(name="service_date", columnDefinition = "date default current_date")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date date;
    @ManyToOne
    @JoinColumn(name = "box_id",referencedColumnName = "id")
    @NotNull
    private Box box;


}
