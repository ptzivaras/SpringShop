package com.eshop.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // e.g. "ROLE_USER", "ROLE_ADMIN"
    @Column(unique = true, nullable = false)
    private String name;
}
