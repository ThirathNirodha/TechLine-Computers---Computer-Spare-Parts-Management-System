package com.techline.spareparts.controller;

import com.techline.spareparts.entity.Category;
import com.techline.spareparts.entity.Product;
import com.techline.spareparts.service.CategoryService;
import com.techline.spareparts.service.ProductService;
import com.techline.spareparts.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SearchHistoryService searchHistoryService;

    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String search,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Boolean lowStockOnly) {
        List<Product> products;
        if (search != null && !search.isBlank()) {
            searchHistoryService.recordSearch(search);
            products = productService.search(search);
        } else if (Boolean.TRUE.equals(lowStockOnly)) {
            products = productService.findLowStock();
        } else if (categoryId != null) {
            products = productService.findByCategoryId(categoryId);
        } else {
            products = productService.findAll();
        }
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("lowStockItems", productService.findLowStock());
        model.addAttribute("recentSearches", searchHistoryService.getRecentSearches());
        model.addAttribute("frequentSearches", searchHistoryService.getFrequentlySearched(10));
        model.addAttribute("title", "Inventory");
        return "inventory/list";
    }

    @GetMapping("/report")
    public String report(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("lowStockItems", productService.findLowStock());
        model.addAttribute("title", "Stock Report");
        return "inventory/report";
    }

    @GetMapping("/new")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("title", "Add Product");
        return "inventory/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.findById(id));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("title", "Edit Product");
        return "inventory/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product, @RequestParam(required = false) Long categoryId) {
        if (categoryId != null) product.setCategory(categoryService.findById(categoryId));
        productService.save(product);
        return "redirect:/inventory";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/inventory";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("title", "Categories");
        return "inventory/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("title", "Add Category");
        return "inventory/category-form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(@ModelAttribute Category category) {
        categoryService.save(category);
        return "redirect:/inventory/categories";
    }
}
