package com.aydnorcn.mis_app.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    public Role(String name) {
        this.name = name;
    }
}