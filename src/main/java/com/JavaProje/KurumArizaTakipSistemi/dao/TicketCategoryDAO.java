package com.JavaProje.KurumArizaTakipSistemi.dao;

import com.JavaProje.KurumArizaTakipSistemi.model.TicketCategory;
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
public class TicketCategoryDAO {

    private static final Logger logger = LoggerFactory.getLogger(TicketCategoryDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public TicketCategory save(TicketCategory category) {
        logger.debug("TicketCategoryDAO.save() - categoryName={}", category.getCategoryName());
        getSession().persist(category);
        return category;
    }

    public void update(TicketCategory category) {
        logger.debug("TicketCategoryDAO.update() - categoryId={}", category.getCategoryId());
        getSession().merge(category);
    }

    public void delete(TicketCategory category) {
        logger.debug("TicketCategoryDAO.delete() - categoryId={}", category.getCategoryId());
        getSession().remove(category);
    }

    public Optional<TicketCategory> findById(Integer id) {
        logger.debug("TicketCategoryDAO.findById() - id={}", id);

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<TicketCategory> cq = cb.createQuery(TicketCategory.class);

        Root<TicketCategory> root = cq.from(TicketCategory.class);

        Predicate condition = cb.equal(root.get("categoryId"), id);

        cq.select(root).where(condition);

        return getSession()
                .createQuery(cq)
                .getResultStream()
                .findFirst();
    }

    public List<TicketCategory> findAll() {
        logger.debug("TicketCategoryDAO.findAll()");

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<TicketCategory> cq = cb.createQuery(TicketCategory.class);

        Root<TicketCategory> root = cq.from(TicketCategory.class);

        cq.select(root)
                .orderBy(cb.asc(root.get("categoryName")));

        return getSession()
                .createQuery(cq)
                .getResultList();
    }

    public Optional<TicketCategory> findByName(String categoryName) {
        logger.debug("TicketCategoryDAO.findByName() - categoryName={}", categoryName);

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<TicketCategory> cq = cb.createQuery(TicketCategory.class);

        Root<TicketCategory> root = cq.from(TicketCategory.class);

        Predicate condition = cb.equal(root.get("categoryName"), categoryName);

        cq.select(root).where(condition);

        return getSession()
                .createQuery(cq)
                .getResultStream()
                .findFirst();
    }
}