package com.JavaProje.KurumArizaTakipSistemi.dao;

import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

//TODO: use criteria query instead of sql queries

@Repository
public class UserDAO {

    private static final Logger logger = LoggerFactory.getLogger(UserDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public void save(User user) {
        logger.debug("UserDAO.save() - email={}", user.getEmail());
        getSession().persist(user);
    }

    public void update(User user) {
        logger.debug("UserDAO.update() - userId={}", user.getUserId());
        getSession().merge(user);
    }

    public void delete(User user) {
        logger.debug("UserDAO.delete() - userId={}", user.getUserId());
        getSession().remove(user);
    }

    public Optional<User> findByEmail(String email) {
        logger.debug("UserDAO.findByEmail() - email={}", email);
        return getSession()
                .createQuery("FROM User u WHERE u.email = :email", User.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public Optional<User> findById(long id)
    {
        logger.debug("UserDAO.findById() - id = {}", id);

        return Optional.ofNullable(getSession().get(User.class, id));
    }

    public List<User> findAll() {
        logger.debug("UserDAO.findAll()");
        return getSession()
                .createQuery("FROM User u", User.class)
                .getResultList();
    }

    public Optional<User> findByVerificationToken(String token) {
        logger.debug("UserDAO.findByVerificationToken() - token={}", token);
        return getSession()
                .createQuery("FROM User u WHERE u.verificationToken = :token", User.class)
                .setParameter("token", token)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        Long count = getSession()
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count != null && count > 0;
    }

    public long countByRole(Role role) {
        Long count = getSession()
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.role = :role", Long.class)
                .setParameter("role", role)
                .getSingleResult();
        return count != null ? count : 0L;
    }
}
