package com.JavaProje.KurumArizaTakipSistemi.service;

import com.JavaProje.KurumArizaTakipSistemi.dao.TicketCategoryDAO;
import com.JavaProje.KurumArizaTakipSistemi.model.TicketCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryCatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryCatalogService.class);

    private static final List<String> DEFAULT_CATEGORIES = List.of(
            "Yazilim",
            "Donanim",
            "Ag",
            "Tesisat",
            "Guvenlik",
            "Malzeme eksikligi",
            "Elektrik"
    );

    private final TicketCategoryDAO categoryDao;

    public CategoryCatalogService(TicketCategoryDAO categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<TicketCategory> ensureDefaultsAndList() {
        List<TicketCategory> existing = categoryDao.findAll();
        if (!existing.isEmpty()) {
            return existing;
        }

        for (String categoryName : DEFAULT_CATEGORIES) {
            TicketCategory category = new TicketCategory();
            category.setCategoryName(categoryName);
            categoryDao.save(category);
            logger.info("Seeded category: {}", categoryName);
        }

        return categoryDao.findAll();
    }
}
