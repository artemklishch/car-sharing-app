package com.example.carsharingapp.repository.rental;

import com.example.carsharingapp.model.Rental;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface RentalRepository
        extends JpaRepository<Rental, Long>, JpaSpecificationExecutor<Rental> {
    @EntityGraph(attributePaths = {"user", "car"})
    Page<Rental> findAll(Specification spec, Pageable pageable);

    @EntityGraph(attributePaths = {"user", "car"})
    Page<Rental> findAllByUserId(Long user_id, Pageable pageable);

    @EntityGraph(attributePaths = "car")
    @Query("SELECT r FROM Rental r WHERE r.id=?1 AND r.user.id=?2")
    Optional<Rental> findByIdAndUserId(Long id, Long userId);

    @EntityGraph(attributePaths = "car")
    Optional<Rental> findById(Long id);

    @EntityGraph(attributePaths = {"user", "car"})
    @Query("SELECT r FROM Rental r WHERE r.actualReturnDate is null AND r.returnDate < :overdueDate")
    List<Rental> findAllOverdue(LocalDate overdueDate);
}
