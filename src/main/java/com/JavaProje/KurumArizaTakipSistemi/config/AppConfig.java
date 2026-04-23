package com.JavaProje.KurumArizaTakipSistemi.config;


import com.JavaProje.KurumArizaTakipSistemi.model.*;
import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScans(value = {
        @ComponentScan("com.JavaProje.KurumArizaTakipSistemi.service"),
        @ComponentScan("com.JavaProje.KurumArizaTakipSistemi.dao")
})
@PropertySource(value = "classpath:hibernate.properties", encoding = "UTF-8")
@PropertySource(value = "classpath:ai.properties", encoding = "UTF-8")
public class AppConfig {

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
    public JavaMailSender getMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(env.getProperty("mail.username"));
        mailSender.setPassword(env.getProperty("mail.password"));

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }
}