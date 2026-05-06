package com.JavaProje.KurumArizaTakipSistemi.config;

import com.JavaProje.KurumArizaTakipSistemi.model.*;
import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.io.InputStream;

import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScans(value = {
        @ComponentScan("com.JavaProje.KurumArizaTakipSistemi.service"),
        @ComponentScan("com.JavaProje.KurumArizaTakipSistemi.dao")
})
@PropertySource(value = "classpath:hibernate-test.properties", encoding = "UTF-8")
public class TestAppConfig {

    @Autowired
    private Environment env;

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();

        Properties props = new Properties();
        // Test properties file uses standard Hibernate keys (hibernate.connection.*)
        putIfPresent(props, AvailableSettings.DRIVER, env.getProperty("hibernate.connection.driver_class"));
        putIfPresent(props, AvailableSettings.URL, env.getProperty("hibernate.connection.url"));
        putIfPresent(props, AvailableSettings.USER, env.getProperty("hibernate.connection.username"));
        putIfPresent(props, AvailableSettings.PASS, env.getProperty("hibernate.connection.password"));

        putIfPresent(props, AvailableSettings.DIALECT, env.getProperty("hibernate.dialect"));
        putIfPresent(props, AvailableSettings.HBM2DDL_AUTO, env.getProperty("hibernate.hbm2ddl.auto"));
        putIfPresent(props, AvailableSettings.SHOW_SQL, env.getProperty("hibernate.show_sql"));
        putIfPresent(props, AvailableSettings.FORMAT_SQL, env.getProperty("hibernate.format_sql"));

        // Optional pool settings (avoid NPE when not set in test properties)
        putIfPresent(props, AvailableSettings.C3P0_MIN_SIZE, env.getProperty("hibernate.c3p0.min_size"));
        putIfPresent(props, AvailableSettings.C3P0_MAX_SIZE, env.getProperty("hibernate.c3p0.max_size"));
        putIfPresent(props, AvailableSettings.C3P0_ACQUIRE_INCREMENT, env.getProperty("hibernate.c3p0.acquire_increment"));
        putIfPresent(props, AvailableSettings.C3P0_TIMEOUT, env.getProperty("hibernate.c3p0.timeout"));
        putIfPresent(props, AvailableSettings.C3P0_MAX_STATEMENTS, env.getProperty("hibernate.c3p0.max_statements"));

        factoryBean.setHibernateProperties(props);
        factoryBean.setAnnotatedClasses(
                Role.class,
                User.class,
                Ticket.class,
                TicketCategory.class,
                TicketStatus.class
        );

        return factoryBean;
    }

    private static void putIfPresent(Properties props, String key, String value) {
        if (key == null) return;
        if (value == null) return;
        String trimmed = value.trim();
        if (trimmed.isEmpty()) return;
        props.put(key, trimmed);
    }

    @Bean
    public HibernateTransactionManager getTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(getSessionFactory().getObject());
        return transactionManager;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSender() {
            @Override
            public MimeMessage createMimeMessage() {
                return new MimeMessage((Session) null);
            }

            @Override
            public MimeMessage createMimeMessage(@NonNull InputStream contentStream) {
                return new MimeMessage((Session) null);
            }

            @Override
            public void send(@NonNull MimeMessage mimeMessage) {}

            @Override
            public void send(@NonNull MimeMessage... mimeMessages) {}

            @Override
            public void send(@NonNull SimpleMailMessage simpleMessage) {}

            @Override
            public void send(@NonNull SimpleMailMessage... simpleMessages) {}
        };
    }
}
