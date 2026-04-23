package com.JavaProje.KurumArizaTakipSistemi.service;


import com.JavaProje.KurumArizaTakipSistemi.dao.RoleDAO;
import com.JavaProje.KurumArizaTakipSistemi.dao.UserDAO;
import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private EmailService emailService;

    private String hashPassword(String password) {
        return org.springframework.util.DigestUtils.md5DigestAsHex(password.getBytes());
    }

    private boolean matchesPassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }

    private List<String> allowedEmailDomains = List.of("ogr.duzce.edu.tr", "gmail.com");


    @Transactional
    public void registerUser(String fullName, String email, String password) {
        logger.info("UserService.registerUser() - email={}", email);

        boolean isAllowed = false;
        for(String allowedEmail: allowedEmailDomains)
        {
            if (email.toLowerCase().endsWith("@" + allowedEmail.toLowerCase()))
            {
                isAllowed = true;
                break;
            }
        }
        if(!isAllowed) throw new IllegalArgumentException("bu uzantılı uzantılı e-posta adresleri kabul edilmektedir.");


        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("Bu e-posta adresi zaten kayıtlı.");
        }

        Role userRole = roleDAO.findByName("USER").orElseGet(() -> {
            Role newRole = new Role();
            newRole.setRoleName("USER");
            roleDAO.save(newRole);
            return newRole;
        });

        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPasswordHash(hashPassword(password));
        user.setRole(userRole);
        user.setVerified(false);

        String token = UUID.randomUUID().toString().replace("-", "");
        user.setVerificationToken(token);
        user.setTokenExpiresAt(LocalDateTime.now().plusHours(24));

        userDAO.save(user);
        logger.info("Kullanıcı kaydedildi | email={}", email);

        emailService.sendVerificationEmail(email, fullName, token);
    }

    @Transactional
    public boolean verifyEmail(String token) {
        logger.info("UserService.verifyEmail() - token={}", token);

        return userDAO.findByVerificationToken(token).map(user -> {
            if (user.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
                logger.warn("Token süresi dolmuş | email={}", user.getEmail());
                return false;
            }
            user.setVerified(true);
            user.setVerificationToken(null);
            user.setTokenExpiresAt(null);
            userDAO.update(user);
            logger.info("Mail doğrulandı | email={}", user.getEmail());
            return true;
        }).orElse(false);
    }



    @Transactional(readOnly = true)
    public User login(String email, String password) {
        logger.info("UserService.login() - email={}", email);

        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("E-posta veya şifre hatalı."));

        if (!user.getVerified()) {
            throw new IllegalArgumentException("E-posta adresiniz henüz doğrulanmamış.");
        }

        if (!matchesPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("E-posta veya şifre hatalı.");
        }

        logger.info("Giriş başarılı | email={}", email);
        return user;
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        logger.info("UserService.resendVerificationEmail() - email={}", email);

        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Bu e-posta adresi kayıtlı değil."));

        if (user.getVerified()) {
            throw new IllegalArgumentException("Bu hesap zaten doğrulanmış.");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        user.setVerificationToken(token);
        user.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userDAO.update(user);

        emailService.sendVerificationEmail(email, user.getFullName(), token);
        logger.info("Doğrulama maili yeniden gönderildi | email={}", email);
    }

    @Transactional
    public List<User> loadAllUsers()
    {
        logger.info("UserService.loadAllUsers()");
        return userDAO.findAll();
    }

    @Transactional
    public boolean setRole(long userId, long roleId)
    {
        logger.info("UserService.setRole() - userId={} roleId={}", userId, roleId);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("This user doesn't exists"));

        if (roleId <= 0) {
            throw new IllegalArgumentException("Role id is invalid");
        }

        Role persistedRole = roleDAO.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("This role doesn't exist"));

        user.setRole(persistedRole);
        userDAO.update(user);
        return true;
    }

    @Transactional
    public void deleteUser(long id) {
        logger.info("UserService.deleteUser() - id={}", id);

        User user = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("This user doesn't exist"));

        userDAO.delete(user);
    }

    @Transactional
    public void addRole(String roleName) {
        logger.info("UserService.addRole() - role={}", roleName);

        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be blank");
        }

        String normalizedRoleName = roleName.trim().toUpperCase();
        if (roleDAO.findByName(normalizedRoleName).isPresent()) {
            throw new IllegalArgumentException("This role already exists");
        }

        Role role = new Role();
        role.setRoleName(normalizedRoleName);
        roleDAO.save(role);
    }

    @Transactional
    public void deleteRole(String roleName) {
        logger.info("UserService.deleteRole() - role={}", roleName);

        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("Role name cannot be blank");
        }

        Role role = roleDAO.findByName(roleName.trim().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("This role doesn't exist"));

        if (userDAO.countByRole(role) > 0) {
            throw new IllegalArgumentException("This role is assigned to users");
        }

        roleDAO.delete(role);
    }

    @Transactional(readOnly = true)
    public List<Role> loadAllRoles() {
        logger.info("UserService.loadAllRoles()");
        return roleDAO.findAll();
    }
}
