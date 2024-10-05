package com.example.carsharingapp.repository.payment;

import com.example.carsharingapp.model.Payment;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends
        JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {
    @EntityGraph(attributePaths = {"rental.car", "rental.user"})
    Page<Payment> findAll(Specification spec, Pageable pageable);

    @EntityGraph(attributePaths = {"rental.car", "rental.user"})
    @Query("SELECT p FROM Payment p WHERE p.rental.user.id=:userId")
    Page<Payment> findAllByUserId(Pageable pageable, Long userId);

    Optional<Payment> findBySessionId(String sessionId);
}
