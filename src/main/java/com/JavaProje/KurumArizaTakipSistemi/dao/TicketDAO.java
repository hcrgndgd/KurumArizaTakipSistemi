package com.JavaProje.KurumArizaTakipSistemi.dao;

import com.JavaProje.KurumArizaTakipSistemi.model.Ticket;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TicketDAO {

    private static final Logger logger = LoggerFactory.getLogger(TicketDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public Ticket save(Ticket ticket) {
        logger.debug("TicketDAO.save() - title={}", ticket.getTitle());
        getSession().persist(ticket);
        return ticket;
    }

    public void update(Ticket ticket) {
        logger.debug("TicketDAO.update() - ticketId={}", ticket.getTicketId());
        getSession().merge(ticket);
    }

    public void delete(Ticket ticket) {
        logger.debug("TicketDAO.delete() - ticketId={}", ticket.getTicketId());
        getSession().remove(ticket);
    }

    public Optional<Ticket> findById(Integer id) {
        logger.debug("TicketDAO.findById() - id={}", id);

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        root.fetch("category", JoinType.LEFT);
        root.fetch("status", JoinType.LEFT);
        root.fetch("requester", JoinType.LEFT);
        root.fetch("assignedTechnician", JoinType.LEFT);

        cq.select(root).distinct(true)
                .where(cb.equal(root.get("ticketId"), id));

        return getSession()
                .createQuery(cq)
                .uniqueResultOptional();
    }

    public List<Ticket> findAll() {
        logger.debug("TicketDAO.findAll()");

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        root.fetch("category", JoinType.LEFT);
        root.fetch("status", JoinType.LEFT);
        root.fetch("requester", JoinType.LEFT);
        root.fetch("assignedTechnician", JoinType.LEFT);

        cq.select(root).distinct(true)
                .orderBy(cb.desc(root.get("createdAt")));

        return getSession()
                .createQuery(cq)
                .getResultList();
    }

    public List<Ticket> findByRequesterId(Long userId) {
        logger.debug("TicketDAO.findByRequesterId() - userId={}", userId);

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        root.fetch("category", JoinType.LEFT);
        root.fetch("status", JoinType.LEFT);
        root.fetch("requester", JoinType.LEFT);
        root.fetch("assignedTechnician", JoinType.LEFT);

        cq.select(root).distinct(true)
                .where(cb.equal(root.get("requester").get("userId"), userId))
                .orderBy(cb.desc(root.get("createdAt")));

        return getSession()
                .createQuery(cq)
                .getResultList();
    }

    public List<Ticket> findByAssignedTechnicianId(Long technicianId) {
        logger.debug("TicketDAO.findByAssignedTechnicianId() - technicianId={}", technicianId);

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Ticket> cq = cb.createQuery(Ticket.class);
        Root<Ticket> root = cq.from(Ticket.class);

        root.fetch("category", JoinType.LEFT);
        root.fetch("status", JoinType.LEFT);
        root.fetch("requester", JoinType.LEFT);
        root.fetch("assignedTechnician", JoinType.LEFT);

        cq.select(root).distinct(true)
                .where(cb.equal(root.get("assignedTechnician").get("userId"), technicianId))
                .orderBy(cb.desc(root.get("createdAt")));

        return getSession()
                .createQuery(cq)
                .getResultList();
    }

    public long countByRequesterId(Long userId) {
        logger.debug("TicketDAO.countByRequesterId() - userId={}", userId);

        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Ticket> root = cq.from(Ticket.class);

        cq.select(cb.count(root))
                .where(cb.equal(root.get("requester").get("userId"), userId));

        Long count = getSession().createQuery(cq).getSingleResult();
        return count != null ? count : 0L;
    }

    public void nullifyRequesterByUserId(long userId) {
        logger.debug("TicketDAO.nullifyRequesterByUserId() - userId={}", userId);
        getSession()
                .createMutationQuery("UPDATE Ticket t SET t.requester = null WHERE t.requester.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }

    public void nullifyAssignedTechnicianByUserId(long userId) {
        logger.debug("TicketDAO.nullifyAssignedTechnicianByUserId() - userId={}", userId);
        getSession()
                .createMutationQuery("UPDATE Ticket t SET t.assignedTechnician = null WHERE t.assignedTechnician.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}