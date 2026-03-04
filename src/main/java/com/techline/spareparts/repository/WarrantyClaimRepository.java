package com.techline.spareparts.repository;

import com.techline.spareparts.entity.WarrantyClaim;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarrantyClaimRepository extends JpaRepository<WarrantyClaim, Long> {
}
