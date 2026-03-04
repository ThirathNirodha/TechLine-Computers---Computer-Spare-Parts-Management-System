package com.techline.spareparts.config;

import com.techline.spareparts.entity.Category;
import com.techline.spareparts.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds default categories (RAM, SSD, Motherboard, CPU, GPU, PSU) so the product form has options to select.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    private static final List<String[]> DEFAULT_CATEGORIES = List.of(
            new String[]{"RAM", "Memory modules"},
            new String[]{"SSD", "Solid state drives"},
            new String[]{"Motherboard", "Motherboards"},
            new String[]{"CPU", "Processors"},
            new String[]{"GPU", "Graphics cards"},
            new String[]{"PSU", "Power supply units"}
    );

    @Override
    public void run(ApplicationArguments args) {
        for (String[] nameAndDesc : DEFAULT_CATEGORIES) {
            if (categoryRepository.findByName(nameAndDesc[0]).isEmpty()) {
                categoryRepository.save(Category.builder()
                        .name(nameAndDesc[0])
                        .description(nameAndDesc[1])
                        .build());
            }
        }
    }
}
