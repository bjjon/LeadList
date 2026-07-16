package org.bjjon.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "status")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Status {

    @Id
    @Column(name = "value", nullable = false, length = 50)
    private String value;

    @Column(name = "label", nullable = false, length = 100)
    private String label;

    @Column(name = "color", nullable = false, length = 50)
    private String color;
}