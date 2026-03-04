package com.techline.spareparts.controller;

import com.techline.spareparts.entity.PurchaseOrder;
import com.techline.spareparts.entity.Supplier;
import com.techline.spareparts.entity.SupplierPayment;
import com.techline.spareparts.service.ProductService;
import com.techline.spareparts.service.PurchaseOrderService;
import com.techline.spareparts.service.SupplierPaymentService;
import com.techline.spareparts.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Controller
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;
    private final PurchaseOrderService purchaseOrderService;
    private final SupplierPaymentService supplierPaymentService;
    private final ProductService productService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("title", "Suppliers");
        return "suppliers/list";
    }

    @GetMapping("/new")
    public String newSupplier(Model model) {
        model.addAttribute("supplier", new Supplier());
        model.addAttribute("title", "Add Supplier");
        return "suppliers/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.findById(id));
        model.addAttribute("title", "Edit Supplier");
        return "suppliers/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Supplier supplier) {
        supplierService.save(supplier);
        return "redirect:/suppliers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        supplierService.deleteById(id);
        return "redirect:/suppliers";
    }

    @GetMapping("/{id}/orders")
    public String orders(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.findById(id));
        model.addAttribute("orders", purchaseOrderService.findAll().stream()
                .filter(po -> po.getSupplier().getId().equals(id))
                .toList());
        model.addAttribute("title", "Purchase Orders");
        return "suppliers/orders";
    }

    @GetMapping("/orders/new")
    public String newOrder(Model model) {
        PurchaseOrder order = new PurchaseOrder();
        order.setItems(new ArrayList<>());
        model.addAttribute("order", order);
        model.addAttribute("suppliers", supplierService.findAll());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("title", "New Purchase Order");
        return "suppliers/order-form";
    }

    @GetMapping("/{id}/payments")
    public String payments(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.findById(id));
        model.addAttribute("payments", supplierPaymentService.findBySupplierId(id));
        model.addAttribute("title", "Payment History");
        return "suppliers/payments";
    }

    @PostMapping("/orders/save")
    public String saveOrder(@RequestParam Long supplierId, @RequestParam String items) {
        if (items == null || items.isBlank()) return "redirect:/suppliers/orders/new?error=empty";
        purchaseOrderService.createOrder(supplierId, items);
        return "redirect:/suppliers";
    }

    @GetMapping("/orders")
    public String allOrders(Model model) {
        model.addAttribute("orders", purchaseOrderService.findAll());
        model.addAttribute("title", "All Purchase Orders");
        return "suppliers/order-list";
    }
}
