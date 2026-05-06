package com.JavaProje.KurumArizaTakipSistemi.dao;

import com.JavaProje.KurumArizaTakipSistemi.model.Ticket;
import com.JavaProje.KurumArizaTakipSistemi.model.TicketCategory;
import com.JavaProje.KurumArizaTakipSistemi.model.TicketStatus;
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
        return getSession()
                .createQuery("SELECT DISTINCT t FROM Ticket t " +
                        "LEFT JOIN FETCH t.category " +
                        "LEFT JOIN FETCH t.status " +
                        "LEFT JOIN FETCH t.requester " +
                        "LEFT JOIN FETCH t.assignedTechnician " +
                        "WHERE t.ticketId = :id", Ticket.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    public List<Ticket> findAll() {
        logger.debug("TicketDAO.findAll()");
        return getSession()
                .createQuery("SELECT DISTINCT t FROM Ticket t " +
                        "LEFT JOIN FETCH t.category " +
                        "LEFT JOIN FETCH t.status " +
                        "LEFT JOIN FETCH t.requester " +
                        "LEFT JOIN FETCH t.assignedTechnician " +
                        "ORDER BY t.createdAt DESC", Ticket.class)
                .getResultList();
    }

    public List<Ticket> findByRequesterId(Long userId) {
        logger.debug("TicketDAO.findByRequesterId() - userId={}", userId);
        return getSession()
                .createQuery("SELECT DISTINCT t FROM Ticket t " +
                        "LEFT JOIN FETCH t.category " +
                        "LEFT JOIN FETCH t.status " +
                        "LEFT JOIN FETCH t.requester " +
                        "LEFT JOIN FETCH t.assignedTechnician " +
                        "WHERE t.requester.userId = :userId " +
                        "ORDER BY t.createdAt DESC", Ticket.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    public List<Ticket> findByAssignedTechnicianId(Long technicianId) {
        logger.debug("TicketDAO.findByAssignedTechnicianId() - technicianId={}", technicianId);
        return getSession()
                .createQuery("SELECT DISTINCT t FROM Ticket t " +
                        "LEFT JOIN FETCH t.category " +
                        "LEFT JOIN FETCH t.status " +
                        "LEFT JOIN FETCH t.requester " +
                        "LEFT JOIN FETCH t.assignedTechnician " +
                        "WHERE t.assignedTechnician.userId = :technicianId " +
                        "ORDER BY t.createdAt DESC", Ticket.class)
                .setParameter("technicianId", technicianId)
                .getResultList();
    }

    public List<Ticket> findByStatus(TicketStatus status) {
        logger.debug("TicketDAO.findByStatus() - statusId={}", status.getStatusId());
        return getSession()
                .createQuery("SELECT DISTINCT t FROM Ticket t " +
                        "LEFT JOIN FETCH t.category " +
                        "LEFT JOIN FETCH t.status " +
                        "LEFT JOIN FETCH t.requester " +
                        "WHERE t.status = :status " +
                        "ORDER BY t.createdAt DESC", Ticket.class)
                .setParameter("status", status)
                .getResultList();
    }

    public List<Ticket> findByCategory(TicketCategory category) {
        logger.debug("TicketDAO.findByCategory() - categoryId={}", category.getCategoryId());
        return getSession()
                .createQuery("SELECT DISTINCT t FROM Ticket t " +
                        "LEFT JOIN FETCH t.category " +
                        "LEFT JOIN FETCH t.status " +
                        "LEFT JOIN FETCH t.requester " +
                        "WHERE t.category = :category " +
                        "ORDER BY t.createdAt DESC", Ticket.class)
                .setParameter("category", category)
                .getResultList();
    }

    public long countAll() {
        logger.debug("TicketDAO.countAll()");
        Long count = getSession()
                .createQuery("SELECT COUNT(t) FROM Ticket t", Long.class)
                .getSingleResult();
        return count != null ? count : 0L;
    }

    public long countByRequesterId(Long userId) {
        logger.debug("TicketDAO.countByRequesterId() - userId={}", userId);
        Long count = getSession()
                .createQuery("SELECT COUNT(t) FROM Ticket t WHERE t.requester.userId = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count != null ? count : 0L;
    }

    public int deleteExpiredUnverifiedUsers() {
        return getSession()
                .createMutationQuery(
                        "DELETE FROM Ticket t WHERE t.assignedTechnician IS NULL " +
                                "AND t.createdAt < :cutoff")
                .setParameter("cutoff", java.time.LocalDateTime.now().minusDays(30))
                .executeUpdate();
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
