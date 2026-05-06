package com.JavaProje.KurumArizaTakipSistemi.service;

import com.JavaProje.KurumArizaTakipSistemi.dao.RoleDAO;
import com.JavaProje.KurumArizaTakipSistemi.dao.TicketDAO;
import com.JavaProje.KurumArizaTakipSistemi.dao.UserDAO;
import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final List<String> DEFAULT_ROLE_NAMES = List.of("USER", "ADMIN", "TECHNICIAN");

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private EmailService emailService;

    @Autowired
    private Environment env;

    @Autowired
    private TicketDAO ticketDAO;

    private String hashPassword(String password) {
        return org.springframework.util.DigestUtils.md5DigestAsHex(password.getBytes());
    }

    private boolean matchesPassword(String rawPassword, String hashedPassword) {
        return hashPassword(rawPassword).equals(hashedPassword);
    }

    private final List<String> allowedEmailDomains = List.of("ogr.duzce.edu.tr");

    @Transactional
    public void registerUser(String fullName, String email, String password) {
        logger.info("UserService.registerUser() - email={}", email);

        boolean isAllowed = false;
        for (String allowedEmail : allowedEmailDomains) {
            if (email.toLowerCase().endsWith("@" + allowedEmail.toLowerCase())) {
                isAllowed = true;
                break;
            }
        }
        if (!isAllowed) throw new IllegalArgumentException("error.email.not.allowed");

        if (userDAO.existsByEmail(email)) {
            throw new IllegalArgumentException("error.email.exists");
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
                .orElseThrow(() -> new IllegalArgumentException("error.password.wrong"));

        if (!user.getVerified()) {
            throw new IllegalArgumentException("error.email.not.verified");
        }

        if (!matchesPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("error.password.wrong");
        }

        logger.info("Giriş başarılı | email={}", email);
        return user;
    }

    @Transactional
    public void resendVerificationEmail(String email) {
        logger.info("UserService.resendVerificationEmail() - email={}", email);

        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("error.email.not.found"));

        if (user.getVerified()) {
            throw new IllegalArgumentException("error.email.already.verified");
        }

        String token = UUID.randomUUID().toString().replace("-", "");
        user.setVerificationToken(token);
        user.setTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userDAO.update(user);

        emailService.sendVerificationEmail(email, user.getFullName(), token);
        logger.info("Doğrulama maili yeniden gönderildi | email={}", email);
    }

    @Transactional
    public List<User> loadAllUsers() {
        logger.info("UserService.loadAllUsers()");
        return userDAO.findAll();
    }


    @Transactional
    public void setRole(long userId, long roleId) {
        logger.info("UserService.setRole() - userId={} roleId={}", userId, roleId);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("error.user.not.found"));

        if (roleId <= 0) {
            throw new IllegalArgumentException("error.role.invalid");
        }

        Role persistedRole = roleDAO.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("error.role.not.found"));

        user.setRole(persistedRole);
        userDAO.update(user);
    }

    @Transactional
    public void deleteUser(long id) {
        logger.info("UserService.deleteUser() - id={}", id);

        User user = userDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("error.user.not.found"));

        ticketDAO.nullifyRequesterByUserId(id);
        ticketDAO.nullifyAssignedTechnicianByUserId(id);

        userDAO.delete(user);
    }

    @Transactional
    public void addRole(String roleName) {
        logger.info("UserService.addRole() - role={}", roleName);

        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("error.role.blank");
        }

        String normalizedRoleName = roleName.trim().toUpperCase();
        if (roleDAO.findByName(normalizedRoleName).isPresent()) {
            throw new IllegalArgumentException("error.role.exists");
        }

        Role role = new Role();
        role.setRoleName(normalizedRoleName);
        roleDAO.save(role);
    }

    @Transactional
    public void deleteRole(String roleName) {
        logger.info("UserService.deleteRole() - role={}", roleName);

        if (roleName == null || roleName.isBlank()) {
            throw new IllegalArgumentException("error.role.blank");
        }

        Role role = roleDAO.findByName(roleName.trim().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("error.role.not.found"));

        if (userDAO.countByRole(role) > 0) {
            throw new IllegalArgumentException("error.role.assigned");
        }

        roleDAO.delete(role);
    }

    @Transactional(readOnly = true)
    public List<Role> loadAllRoles() {
        logger.info("UserService.loadAllRoles()");
        return roleDAO.findAll();
    }

    @Transactional
    public void ensureDefaultRoles() {
        logger.info("REFERENCE_DATA_SEED | class=UserService | method=ensureDefaultRoles | target=roles | required={}", DEFAULT_ROLE_NAMES);

        List<Role> existingRoles = roleDAO.findAll();
        for (String roleName : DEFAULT_ROLE_NAMES) {
            Role existingRole = existingRoles.stream()
                    .filter(role -> role.getRoleName() != null && roleName.equalsIgnoreCase(role.getRoleName().trim()))
                    .findFirst()
                    .orElse(null);

            if (existingRole == null) {
                Role role = new Role();
                role.setRoleName(roleName);
                roleDAO.save(role);
                logger.info("REFERENCE_DATA_CREATED | class=UserService | method=ensureDefaultRoles | role={}", roleName);
            } else if (!roleName.equals(existingRole.getRoleName())) {
                String previousRoleName = existingRole.getRoleName();
                existingRole.setRoleName(roleName);
                roleDAO.update(existingRole);
                logger.info("REFERENCE_DATA_NORMALIZED | class=UserService | method=ensureDefaultRoles | previousRole={} | normalizedRole={}", previousRoleName, roleName);
            } else {
                logger.debug("REFERENCE_DATA_EXISTS | class=UserService | method=ensureDefaultRoles | role={}", roleName);
            }
        }
    }

    @Transactional
    public void ensureDefaultAdminUser() {
        boolean bootstrapEnabled = Boolean.parseBoolean(env.getProperty("admin.bootstrap.enabled", "true"));
        if (!bootstrapEnabled) {
            logger.info("Default admin bootstrap is disabled");
            return;
        }

        String email = env.getProperty("admin.email");
        String password = env.getProperty("admin.password");
        String fullName = env.getProperty("admin.full-name", "System Admin");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            logger.warn("Default admin bootstrap skipped because admin.email or admin.password is missing");
            return;
        }

        if (userDAO.existsByEmail(email)) {
            logger.info("Default admin bootstrap skipped because user already exists | email={}", email);
            return;
        }

        Role adminRole = roleDAO.findByName("ADMIN").orElseGet(() -> {
            Role role = new Role();
            role.setRoleName("ADMIN");
            roleDAO.save(role);
            return role;
        });

        User admin = new User();
        admin.setFullName(fullName);
        admin.setEmail(email);
        admin.setPasswordHash(hashPassword(password));
        admin.setVerified(true);
        admin.setRole(adminRole);

        userDAO.save(admin);
        logger.warn("Default admin user created. Change the bootstrap password after first login | email={}", email);
    }

    @Transactional
    public void sendPasswordResetEmail(String email) {
        logger.info("UserService.sendPasswordResetEmail() - email={}", email);

        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("error.email.not.found"));

        String token = UUID.randomUUID().toString().replace("-", "");
        user.setVerificationToken(token);
        user.setTokenExpiresAt(LocalDateTime.now().plusHours(1));
        userDAO.update(user);

        emailService.sendPasswordResetEmail(email, user.getFullName(), token);
        logger.info("Şifre sıfırlama maili gönderildi | email={}", email);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        logger.info("UserService.resetPassword() - token={}", token);

        User user = userDAO.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("error.token.invalid"));

        if (user.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("error.token.expired");
        }

        user.setPasswordHash(hashPassword(newPassword));
        user.setVerificationToken(null);
        user.setTokenExpiresAt(null);
        userDAO.update(user);
        logger.info("Şifre sıfırlandı | email={}", user.getEmail());
    }
}