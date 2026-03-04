package com.techline.spareparts.service;

import com.techline.spareparts.entity.ServiceTicket;
import com.techline.spareparts.repository.ServiceTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTicketService {

    private final ServiceTicketRepository serviceTicketRepository;

    public List<ServiceTicket> findAll() {
        return serviceTicketRepository.findAll();
    }

    public ServiceTicket findById(Long id) {
        return serviceTicketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service ticket not found: " + id));
    }

    public List<ServiceTicket> findByCustomerId(Long customerId) {
        return serviceTicketRepository.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    @Transactional
    public ServiceTicket save(ServiceTicket ticket) {
        if (ticket.getCreatedAt() == null) ticket.setCreatedAt(java.time.LocalDateTime.now());
        return serviceTicketRepository.save(ticket);
    }

    @Transactional
    public void deleteById(Long id) {
        serviceTicketRepository.deleteById(id);
    }
}
