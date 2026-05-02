package com.JavaProje.KurumArizaTakipSistemi.dao;

import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);

        Root<User> root = cq.from(User.class);

        Predicate condition = cb.equal(root.get("email"), email);

        cq.select(root).where(condition);

        return getSession()
                .createQuery(cq)
                .getResultStream()
                .findFirst();
    }

    public Optional<User> findById(long id) {
        logger.debug("UserDAO.findById() - id = {}", id);
        return Optional.ofNullable(getSession().get(User.class, id));
    }

    public List<User> findAll() {
        logger.debug("UserDAO.findAll()");

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);

        Root<User> root = cq.from(User.class);

        cq.select(root);

        return getSession()
                .createQuery(cq)
                .getResultList();
    }

    public Optional<User> findByVerificationToken(String token) {
        logger.debug("UserDAO.findByVerificationToken() - token={}", token);

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);

        Root<User> root = cq.from(User.class);

        Predicate condition = cb.equal(root.get("verificationToken"), token);

        cq.select(root).where(condition);

        return getSession()
                .createQuery(cq)
                .getResultStream()
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<User> root = cq.from(User.class);

        cq.select(cb.count(root))
                .where(cb.equal(root.get("email"), email));

        Long count = getSession().createQuery(cq).getSingleResult();

        return count != null && count > 0;
    }

    public long countByRole(Role role) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);

        Root<User> root = cq.from(User.class);

        cq.select(cb.count(root))
                .where(cb.equal(root.get("role"), role));

        Long count = getSession().createQuery(cq).getSingleResult();

        return count != null ? count : 0L;
    }

    public int deleteExpiredUnverifiedUsers() {
        return getSession()
                .createMutationQuery(
                        "DELETE FROM User u WHERE u.Verified = false " +
                                "AND u.tokenExpiresAt < :now")
                .setParameter("now", java.time.LocalDateTime.now())
                .executeUpdate();
    }
}