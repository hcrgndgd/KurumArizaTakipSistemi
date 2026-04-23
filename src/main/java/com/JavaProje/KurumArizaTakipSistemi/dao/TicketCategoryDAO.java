package com.JavaProje.KurumArizaTakipSistemi.dao;

import com.JavaProje.KurumArizaTakipSistemi.model.TicketCategory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for TicketCategory entity.
 */
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
        return Optional.ofNullable(getSession().get(TicketCategory.class, id));
    }

    public List<TicketCategory> findAll() {
        logger.debug("TicketCategoryDAO.findAll()");
        return getSession()
                .createQuery("FROM TicketCategory ORDER BY categoryName", TicketCategory.class)
                .getResultList();
    }

    public Optional<TicketCategory> findByName(String categoryName) {
        logger.debug("TicketCategoryDAO.findByName() - categoryName={}", categoryName);
        return getSession()
                .createQuery("FROM TicketCategory c WHERE c.categoryName = :name", TicketCategory.class)
                .setParameter("name", categoryName)
                .getResultStream()
                .findFirst();
    }
}
