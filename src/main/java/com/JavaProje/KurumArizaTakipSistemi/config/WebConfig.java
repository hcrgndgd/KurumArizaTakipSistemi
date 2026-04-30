package com.JavaProje.KurumArizaTakipSistemi.config;

import com.JavaProje.KurumArizaTakipSistemi.interceptor.AdminInterceptor;
import com.JavaProje.KurumArizaTakipSistemi.interceptor.AuthInterceptor;
import com.JavaProje.KurumArizaTakipSistemi.interceptor.RequestLoggingInterceptor;
import com.JavaProje.KurumArizaTakipSistemi.interceptor.TechnicianInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
@EnableWebMvc
@ComponentScan("com.JavaProje.KurumArizaTakipSistemi.web")
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/view/", ".jsp");
    }

    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver("lang");
        resolver.setDefaultLocale(new Locale("tr", "TR"));
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    public AdminInterceptor adminInterceptor() {
        return new AdminInterceptor();
    }

    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }

    @Bean
    public TechnicianInterceptor technicianInterceptor() {
        return new TechnicianInterceptor();
    }

    @Bean
    public RequestLoggingInterceptor requestLoggingInterceptor() {
        return new RequestLoggingInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(requestLoggingInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(authInterceptor()).addPathPatterns("/user/**");
        registry.addInterceptor(adminInterceptor()).addPathPatterns("/admin/**");
        registry.addInterceptor(technicianInterceptor()).addPathPatterns("/technician/**");
    }
}
