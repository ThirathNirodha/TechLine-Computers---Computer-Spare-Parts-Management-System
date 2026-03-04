package com.techline.spareparts.repository;

import com.techline.spareparts.entity.ServiceTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, Long> {
    List<ServiceTicket> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
}
