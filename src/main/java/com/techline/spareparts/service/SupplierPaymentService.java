package com.techline.spareparts.service;

import com.techline.spareparts.entity.SupplierPayment;
import com.techline.spareparts.repository.SupplierPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierPaymentService {

    private final SupplierPaymentRepository supplierPaymentRepository;

    public List<SupplierPayment> findBySupplierId(Long supplierId) {
        return supplierPaymentRepository.findBySupplierIdOrderByPaymentDateDesc(supplierId);
    }

    @Transactional
    public SupplierPayment save(SupplierPayment payment) {
        return supplierPaymentRepository.save(payment);
    }
}
