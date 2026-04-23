package com.JavaProje.KurumArizaTakipSistemi.dao;

import com.JavaProje.KurumArizaTakipSistemi.model.TicketStatus;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object for TicketStatus entity.
 */
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
        return Optional.ofNullable(getSession().get(TicketStatus.class, id));
    }

    public List<TicketStatus> findAll() {
        logger.debug("TicketStatusDAO.findAll()");
        return getSession()
                .createQuery("FROM TicketStatus ORDER BY statusName", TicketStatus.class)
                .getResultList();
    }

    public Optional<TicketStatus> findByName(String statusName) {
        logger.debug("TicketStatusDAO.findByName() - statusName={}", statusName);
        return getSession()
                .createQuery("FROM TicketStatus s WHERE s.statusName = :name", TicketStatus.class)
                .setParameter("name", statusName)
                .getResultStream()
                .findFirst();
    }
}
