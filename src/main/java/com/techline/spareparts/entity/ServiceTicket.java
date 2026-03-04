package com.techline.spareparts.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "service_tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @NotBlank
    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceTicketStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;

    public enum ServiceTicketStatus {
        OPEN, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public enum ServiceType {
        REPAIR, RETURN, WARRANTY_CLAIM, OTHER
    }
}
