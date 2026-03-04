package com.techline.spareparts.controller;

import com.techline.spareparts.entity.ServiceTicket;
import com.techline.spareparts.entity.WarrantyClaim;
import com.techline.spareparts.service.CustomerService;
import com.techline.spareparts.service.ProductService;
import com.techline.spareparts.service.SaleService;
import com.techline.spareparts.service.ServiceTicketService;
import com.techline.spareparts.service.WarrantyClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/warranty")
@RequiredArgsConstructor
public class WarrantyController {

    private final ServiceTicketService serviceTicketService;
    private final WarrantyClaimService warrantyClaimService;
    private final CustomerService customerService;
    private final SaleService saleService;
    private final ProductService productService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("tickets", serviceTicketService.findAll());
        model.addAttribute("claims", warrantyClaimService.findAll());
        model.addAttribute("title", "Warranty & Service");
        return "warranty/home";
    }

    @GetMapping("/tickets")
    public String tickets(Model model) {
        model.addAttribute("tickets", serviceTicketService.findAll());
        model.addAttribute("title", "Service Tickets");
        return "warranty/tickets";
    }

    @GetMapping("/tickets/new")
    public String newTicket(Model model) {
        model.addAttribute("ticket", new ServiceTicket());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("sales", saleService.findAll());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("title", "New Service Ticket");
        return "warranty/ticket-form";
    }

    @GetMapping("/tickets/edit/{id}")
    public String editTicket(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", serviceTicketService.findById(id));
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("sales", saleService.findAll());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("title", "Edit Service Ticket");
        return "warranty/ticket-form";
    }

    @PostMapping("/tickets/save")
    public String saveTicket(@ModelAttribute ServiceTicket ticket,
                             @RequestParam(required = false) Long customerId,
                             @RequestParam(required = false) Long saleId,
                             @RequestParam(required = false) Long productId) {
        if (customerId != null) ticket.setCustomer(customerService.findById(customerId));
        if (saleId != null) ticket.setSale(saleService.findById(saleId));
        if (productId != null) ticket.setProduct(productService.findById(productId));
        if (ticket.getStatus() == null) ticket.setStatus(ServiceTicket.ServiceTicketStatus.OPEN);
        serviceTicketService.save(ticket);
        return "redirect:/warranty/tickets";
    }

    @GetMapping("/claims")
    public String claims(Model model) {
        model.addAttribute("claims", warrantyClaimService.findAll());
        model.addAttribute("title", "Warranty Claims");
        return "warranty/claims";
    }

    @GetMapping("/claims/new")
    public String newClaim(Model model) {
        model.addAttribute("claim", new WarrantyClaim());
        model.addAttribute("tickets", serviceTicketService.findAll());
        model.addAttribute("title", "New Warranty Claim");
        return "warranty/claim-form";
    }

    @PostMapping("/claims/save")
    public String saveClaim(@ModelAttribute WarrantyClaim claim,
                           @RequestParam(required = false) Long serviceTicketId,
                           @RequestParam(required = false) Long saleItemId) {
        if (serviceTicketId != null) claim.setServiceTicket(serviceTicketService.findById(serviceTicketId));
        if (claim.getStatus() == null) claim.setStatus(WarrantyClaim.WarrantyClaimStatus.SUBMITTED);
        warrantyClaimService.save(claim);
        return "redirect:/warranty/claims";
    }
}
