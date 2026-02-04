package com.cabbooking.repository;

import com.cabbooking.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    /**
     * Find passenger by the associated user's email.
     * Uses Join behind the scenes.
     */
    Optional<Passenger> findByUserEmail(String email);

    /**
     * Fetch the passenger and the User entity in a single query
     * to avoid lazy loading issues later.
     */
    @EntityGraph(attributePaths = {"user"})
    Optional<Passenger> findWithUserById(Long id);

}