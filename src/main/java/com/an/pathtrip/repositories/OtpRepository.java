package com.an.pathtrip.repositories;

import com.an.pathtrip.enums.OtpType;
import com.an.pathtrip.models.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findTopByUser_EmailAndTypeOrderByCreatedAtDesc(
            String email,
            OtpType type
    );
}

