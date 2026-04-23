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

/**
 * Data Access Object for Ticket entity.
 * Handles all database operations related to tickets.
 */
@Repository
public class TicketDAO {

    private static final Logger logger = LoggerFactory.getLogger(TicketDAO.class);

    @Autowired
    private SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Save a new ticket to the database.
     */
    public Ticket save(Ticket ticket) {
        logger.debug("TicketDAO.save() - title={}", ticket.getTitle());
        getSession().persist(ticket);
        return ticket;
    }

    /**
     * Update an existing ticket.
     */
    public void update(Ticket ticket) {
        logger.debug("TicketDAO.update() - ticketId={}", ticket.getTicketId());
        getSession().merge(ticket);
    }

    /**
     * Delete a ticket.
     */
    public void delete(Ticket ticket) {
        logger.debug("TicketDAO.delete() - ticketId={}", ticket.getTicketId());
        getSession().remove(ticket);
    }

    /**
     * Find ticket by ID.
     */
    public Optional<Ticket> findById(Integer id) {
        logger.debug("TicketDAO.findById() - id={}", id);
        return Optional.ofNullable(getSession().get(Ticket.class, id));
    }

    /**
     * Get all tickets.
     */
    public List<Ticket> findAll() {
        logger.debug("TicketDAO.findAll()");
        return getSession()
                .createQuery("FROM Ticket t ORDER BY t.createdAt DESC", Ticket.class)
                .getResultList();
    }

    /**
     * Find all tickets created by a specific user (requester).
     */
    public List<Ticket> findByRequesterId(Long userId) {
        logger.debug("TicketDAO.findByRequesterId() - userId={}", userId);
        return getSession()
                .createQuery("FROM Ticket t WHERE t.requester.userId = :userId ORDER BY t.createdAt DESC", Ticket.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    /**
     * Find all tickets assigned to a specific technician.
     */
    public List<Ticket> findByAssignedTechnicianId(Long technicianId) {
        logger.debug("TicketDAO.findByAssignedTechnicianId() - technicianId={}", technicianId);
        return getSession()
                .createQuery("FROM Ticket t WHERE t.assignedTechnician.userId = :technicianId ORDER BY t.createdAt DESC", Ticket.class)
                .setParameter("technicianId", technicianId)
                .getResultList();
    }

    /**
     * Find tickets by status.
     */
    public List<Ticket> findByStatus(TicketStatus status) {
        logger.debug("TicketDAO.findByStatus() - statusId={}", status.getStatusId());
        return getSession()
                .createQuery("FROM Ticket t WHERE t.status = :status ORDER BY t.createdAt DESC", Ticket.class)
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * Find tickets by category.
     */
    public List<Ticket> findByCategory(TicketCategory category) {
        logger.debug("TicketDAO.findByCategory() - categoryId={}", category.getCategoryId());
        return getSession()
                .createQuery("FROM Ticket t WHERE t.category = :category ORDER BY t.createdAt DESC", Ticket.class)
                .setParameter("category", category)
                .getResultList();
    }

    /**
     * Count total tickets.
     */
    public long countAll() {
        logger.debug("TicketDAO.countAll()");
        Long count = getSession()
                .createQuery("SELECT COUNT(t) FROM Ticket t", Long.class)
                .getSingleResult();
        return count != null ? count : 0L;
    }

    /**
     * Count tickets for a specific user.
     */
    public long countByRequesterId(Long userId) {
        logger.debug("TicketDAO.countByRequesterId() - userId={}", userId);
        Long count = getSession()
                .createQuery("SELECT COUNT(t) FROM Ticket t WHERE t.requester.userId = :userId", Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count != null ? count : 0L;
    }
}
