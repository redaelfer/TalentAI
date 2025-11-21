package com.talentai.backend.rh;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RhRepository extends JpaRepository<Rh, Long> {
    Optional<Rh> findByUsername(String username);
}