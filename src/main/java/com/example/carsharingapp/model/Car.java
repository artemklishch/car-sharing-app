package com.example.carsharingapp.model;

import com.example.carsharingapp.enums.ModelType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@SQLDelete(sql = "UPDATE cars SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted=false")
@Getter
@Setter
@Entity
@Table(name = "cars")
@Accessors(chain = true)
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private String brand;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelType type;
    private int inventory = 0;
    @Column(name = "daily_fee")
    private BigDecimal dailyFee = BigDecimal.ZERO;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
