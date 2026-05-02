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
        props.put(AvailableSettings.DRIVER, env.getProperty("mysql.driver"));
        props.put(AvailableSettings.URL, env.getProperty("mysql.url"));
        props.put(AvailableSettings.USER, env.getProperty("mysql.user"));
        props.put(AvailableSettings.PASS, env.getProperty("mysql.password"));
        props.put(AvailableSettings.SHOW_SQL, env.getProperty("hibernate.show_sql"));
        props.put(AvailableSettings.HBM2DDL_AUTO, env.getProperty("hibernate.hbm2ddl.auto"));
        props.put(AvailableSettings.DIALECT, env.getProperty("hibernate.dialect"));
        props.put(AvailableSettings.C3P0_MIN_SIZE, env.getProperty("hibernate.c3p0.min_size"));
        props.put(AvailableSettings.C3P0_MAX_SIZE, env.getProperty("hibernate.c3p0.max_size"));
        props.put(AvailableSettings.C3P0_ACQUIRE_INCREMENT, env.getProperty("hibernate.c3p0.acquire_increment"));
        props.put(AvailableSettings.C3P0_TIMEOUT, env.getProperty("hibernate.c3p0.timeout"));
        props.put(AvailableSettings.C3P0_MAX_STATEMENTS, env.getProperty("hibernate.c3p0.max_statements"));

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