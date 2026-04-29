package com.JavaProje.KurumArizaTakipSistemi.dao;

import com.JavaProje.KurumArizaTakipSistemi.model.TicketStatus;
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
public class TicketStatusDAO {

    private static final Logger logger = LoggerFactory.getLogger(TicketStatusDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public TicketStatus save(TicketStatus status) {
        logger.debug("TicketStatusDAO.save() - statusName={}", status.getStatusName());
        getSession().persist(status);
        return status;
    }

    public void update(TicketStatus status) {
        logger.debug("TicketStatusDAO.update() - statusId={}", status.getStatusId());
        getSession().merge(status);
    }

    public void delete(TicketStatus status) {
        logger.debug("TicketStatusDAO.delete() - statusId={}", status.getStatusId());
        getSession().remove(status);
    }

    public Optional<TicketStatus> findById(Integer id) {
        logger.debug("TicketStatusDAO.findById() - id={}", id);

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<TicketStatus> cq = cb.createQuery(TicketStatus.class);

        Root<TicketStatus> root = cq.from(TicketStatus.class);

        Predicate condition = cb.equal(root.get("statusId"), id);

        cq.select(root).where(condition);

        return getSession()
                .createQuery(cq)
                .getResultStream()
                .findFirst();
    }

    public List<TicketStatus> findAll() {
        logger.debug("TicketStatusDAO.findAll()");

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<TicketStatus> cq = cb.createQuery(TicketStatus.class);

        Root<TicketStatus> root = cq.from(TicketStatus.class);

        cq.select(root)
                .orderBy(cb.asc(root.get("statusName")));

        return getSession()
                .createQuery(cq)
                .getResultList();
    }

    public Optional<TicketStatus> findByName(String statusName) {
        logger.debug("TicketStatusDAO.findByName() - statusName={}", statusName);

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<TicketStatus> cq = cb.createQuery(TicketStatus.class);

        Root<TicketStatus> root = cq.from(TicketStatus.class);

        Predicate condition = cb.equal(root.get("statusName"), statusName);

        cq.select(root).where(condition);

        return getSession()
                .createQuery(cq)
                .getResultStream()
                .findFirst();
    }
}