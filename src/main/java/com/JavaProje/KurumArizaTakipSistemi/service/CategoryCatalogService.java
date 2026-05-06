package com.JavaProje.KurumArizaTakipSistemi.service;

import com.JavaProje.KurumArizaTakipSistemi.dao.TicketCategoryDAO;
import com.JavaProje.KurumArizaTakipSistemi.model.TicketCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CategoryCatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryCatalogService.class);

    private static final Map<String, String> DEFAULT_CATEGORIES = Map.of(
            "Yazilim",           "Software",
            "Donanim",           "Hardware",
            "Ag",                "Network",
            "Tesisat",           "Plumbing",
            "Guvenlik",          "Security",
            "Malzeme eksikligi", "Material Shortage",
            "Elektrik",          "Electrical"
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

        for (Map.Entry<String, String> entry : DEFAULT_CATEGORIES.entrySet()) {
            TicketCategory category = new TicketCategory();
            category.setCategoryName(entry.getKey());
            category.setCategoryNameEn(entry.getValue());
            categoryDao.save(category);
            logger.info("Seeded category: {} / {}", entry.getKey(), entry.getValue());
        }

        return categoryDao.findAll();
    }
}