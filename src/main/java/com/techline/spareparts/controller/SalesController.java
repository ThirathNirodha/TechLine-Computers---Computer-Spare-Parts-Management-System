package com.techline.spareparts.controller;

import com.techline.spareparts.entity.Customer;
import com.techline.spareparts.entity.Sale;
import com.techline.spareparts.service.CustomerService;
import com.techline.spareparts.service.InvoicePdfService;
import com.techline.spareparts.service.ProductService;
import com.techline.spareparts.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SaleService saleService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final InvoicePdfService invoicePdfService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("sales", saleService.findAll());
        model.addAttribute("title", "Sales");
        return "sales/list";
    }

    @GetMapping("/new")
    public String newSale(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("cartItems", new ArrayList<CartItem>());
        model.addAttribute("title", "New Sale");
        return "sales/form";
    }

    @GetMapping("/report")
    public String report(Model model, @RequestParam(required = false) String date) {
        LocalDate reportDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        var sales = saleService.findSalesForDate(reportDate);
        BigDecimal total = sales.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("sales", sales);
        model.addAttribute("reportDate", reportDate);
        model.addAttribute("totalSales", total);
        model.addAttribute("title", "Daily Sales Report");
        return "sales/report";
    }

    @PostMapping("/confirm")
    public String confirmSale(@RequestParam(required = false) Long customerId,
                              @RequestParam String items) {
        Customer customer = customerId != null ? customerService.findById(customerId) : null;
        List<SaleService.CartItemDto> cart = parseCartItems(items);
        if (cart.isEmpty()) {
            return "redirect:/sales/new?error=empty";
        }
        Sale sale = saleService.createSale(customer, cart);
        return "redirect:/sales/view/" + sale.getId();
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        model.addAttribute("sale", saleService.findById(id));
        model.addAttribute("title", "Invoice #" + id);
        return "sales/view";
    }

    @GetMapping("/invoice/{id}/pdf")
    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> invoicePdf(@PathVariable Long id) {
        Sale sale = saleService.findById(id);
        byte[] pdf = invoicePdfService.generateInvoicePdf(sale);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    @GetMapping("/customers")
    public String customers(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("title", "Customers");
        return "sales/customers";
    }

    @GetMapping("/customers/new")
    public String newCustomer(Model model) {
        model.addAttribute("customer", new Customer());
        model.addAttribute("title", "Add Customer");
        return "sales/customer-form";
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable Long id, Model model) {
        model.addAttribute("customer", customerService.findById(id));
        model.addAttribute("title", "Edit Customer");
        return "sales/customer-form";
    }

    @PostMapping("/customers/save")
    public String saveCustomer(@ModelAttribute Customer customer) {
        customerService.save(customer);
        return "redirect:/sales/customers";
    }

    private List<SaleService.CartItemDto> parseCartItems(String items) {
        if (items == null || items.isBlank()) return List.of();
        List<SaleService.CartItemDto> result = new ArrayList<>();
        for (String part : items.split(";")) {
            String[] kv = part.split(":");
            if (kv.length == 2) {
                try {
                    result.add(new SaleService.CartItemDto(Long.parseLong(kv[0]), Integer.parseInt(kv[1])));
                } catch (NumberFormatException ignored) {}
            }
        }
        return result;
    }

    public record CartItem(Long productId, int quantity, String productName, String productCode, BigDecimal unitPrice, BigDecimal total) {}
}
