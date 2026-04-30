package com.JavaProje.KurumArizaTakipSistemi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupSeeder {

    private static final Logger logger = LoggerFactory.getLogger(StartupSeeder.class);

    private final CategoryCatalogService categoryCatalogService;
    private final UserService userService;

    @Autowired
    public StartupSeeder(CategoryCatalogService categoryCatalogService, UserService userService) {
        this.categoryCatalogService = categoryCatalogService;
        this.userService = userService;
    }

    @EventListener
    public void onContextRefreshed(ContextRefreshedEvent event) {
        try {
            logger.info("Running CategoryCatalogService.ensureDefaultsAndList() to seed default categories");
            categoryCatalogService.ensureDefaultsAndList();
            logger.info("Category seeding complete");
        } catch (Exception e) {
            logger.warn("Failed to seed categories on startup", e);
        }

        try {
            logger.info("Running UserService.ensureDefaultAdminUser() to seed default admin");
            userService.ensureDefaultAdminUser();
            logger.info("Default admin bootstrap complete");
        } catch (Exception e) {
            logger.warn("Failed to bootstrap default admin user", e);
        }
    }
}
