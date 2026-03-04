package com.techline.spareparts.service;

import com.techline.spareparts.entity.WarrantyClaim;
import com.techline.spareparts.repository.WarrantyClaimRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarrantyClaimService {

    private final WarrantyClaimRepository warrantyClaimRepository;

    public List<WarrantyClaim> findAll() {
        return warrantyClaimRepository.findAll();
    }

    public WarrantyClaim findById(Long id) {
        return warrantyClaimRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warranty claim not found: " + id));
    }

    @Transactional
    public WarrantyClaim save(WarrantyClaim claim) {
        if (claim.getClaimDate() == null) claim.setClaimDate(java.time.LocalDateTime.now());
        return warrantyClaimRepository.save(claim);
    }
}
