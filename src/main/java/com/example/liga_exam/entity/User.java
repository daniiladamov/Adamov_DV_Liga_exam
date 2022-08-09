package com.example.liga_exam.entity;

import com.example.liga_exam.security.RoleEnum;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "users",uniqueConstraints= @UniqueConstraint(columnNames={"username"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    private String surname;

    @NotNull
    private String username;

    @NotNull
    private String password;

    private String uuid;

    @OneToOne(mappedBy = "user")
    private Employee employee;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Fetch(value=FetchMode.SUBSELECT)
    private Set<Order> orders;

    @PostConstruct
    private void setDefaultRole(){
        this.setRole(RoleEnum.ROLE_USER);
    }
}
