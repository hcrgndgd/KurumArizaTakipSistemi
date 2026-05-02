package com.JavaProje.KurumArizaTakipSistemi.service;

import com.JavaProje.KurumArizaTakipSistemi.dao.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CleanupService {

    private static final Logger logger = LoggerFactory.getLogger(CleanupService.class);

    @Autowired
    private UserDAO userDAO;

    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void deleteUnverifiedUsers() {
        logger.info("CleanupService - Doğrulanmamış kullanıcılar temizleniyor...");
        int count = userDAO.deleteExpiredUnverifiedUsers();
        logger.info("CleanupService - {} kullanıcı silindi.", count);
    }
}