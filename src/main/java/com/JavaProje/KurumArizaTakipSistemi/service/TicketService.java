package com.JavaProje.KurumArizaTakipSistemi.service;

import com.JavaProje.KurumArizaTakipSistemi.dao.TicketCategoryDAO;
import com.JavaProje.KurumArizaTakipSistemi.dao.TicketDAO;
import com.JavaProje.KurumArizaTakipSistemi.dao.TicketStatusDAO;
import com.JavaProje.KurumArizaTakipSistemi.dao.UserDAO;
import com.JavaProje.KurumArizaTakipSistemi.model.Ticket;
import com.JavaProje.KurumArizaTakipSistemi.model.TicketCategory;
import com.JavaProje.KurumArizaTakipSistemi.model.TicketStatus;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for Ticket operations.
 * Handles all business logic related to ticket management.
 */
@Service
public class TicketService {

    private static final Logger logger = LoggerFactory.getLogger(TicketService.class);

    @Autowired
    private TicketDAO ticketDAO;

    @Autowired
    private TicketCategoryDAO ticketCategoryDAO;

    @Autowired
    private TicketStatusDAO ticketStatusDAO;

    @Autowired
    private UserDAO userDAO;

    /**
     * Create a new ticket.
     *
     * @param requesterId User ID of the person creating the ticket
     * @param title Ticket title
     * @param description Ticket description
     * @param categoryId Category ID
     * @return Created ticket
     */
    @Transactional
    public Ticket createTicket(Long requesterId, String title, String description, Integer categoryId) {
        logger.info("TicketService.createTicket() - requesterId={}, title={}", requesterId, title);

        // Validate inputs
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket title cannot be empty");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket description cannot be empty");
        }

        // Get requester user
        User requester = userDAO.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get category
        TicketCategory category = ticketCategoryDAO.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        // Get default status (should be "OPEN")
        TicketStatus status = ticketStatusDAO.findByName("OPEN")
                .orElseGet(() -> {
                    logger.warn("OPEN status not found, creating new one");
                    TicketStatus newStatus = new TicketStatus();
                    newStatus.setStatusName("OPEN");
                    return ticketStatusDAO.save(newStatus);
                });

        // Create ticket
        Ticket ticket = new Ticket();
        ticket.setTitle(title);
        ticket.setDescription(description);
        ticket.setRequester(requester);
        ticket.setCategory(category);
        ticket.setStatus(status);
        ticket.setCreatedAt(LocalDateTime.now());

        ticketDAO.save(ticket);
        logger.info("Ticket created successfully - ticketId={}", ticket.getTicketId());

        return ticket;
    }

    /**
     * Get all tickets created by a user.
     *
     * @param userId User ID
     * @return List of tickets
     */
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByRequesterId(Long userId) {
        logger.info("TicketService.getTicketsByRequesterId() - userId={}", userId);
        return ticketDAO.findByRequesterId(userId);
    }

    /**
     * Get all tickets assigned to a technician.
     *
     * @param technicianId User ID of technician
     * @return List of tickets
     */
    @Transactional(readOnly = true)
    public List<Ticket> getTicketsByAssignedTechnician(Long technicianId) {
        logger.info("TicketService.getTicketsByAssignedTechnician() - technicianId={}", technicianId);
        return ticketDAO.findByAssignedTechnicianId(technicianId);
    }

    /**
     * Get all tickets.
     *
     * @return List of all tickets
     */
    @Transactional(readOnly = true)
    public List<Ticket> getAllTickets() {
        logger.info("TicketService.getAllTickets()");
        return ticketDAO.findAll();
    }

    /**
     * Get ticket by ID.
     *
     * @param ticketId Ticket ID
     * @return Ticket if found
     */
    @Transactional(readOnly = true)
    public Ticket getTicketById(Integer ticketId) {
        logger.info("TicketService.getTicketById() - ticketId={}", ticketId);
        return ticketDAO.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    }

    /**
     * Update ticket status.
     *
     * @param ticketId Ticket ID
     * @param statusId Status ID
     */
    @Transactional
    public void updateTicketStatus(Integer ticketId, Integer statusId) {
        logger.info("TicketService.updateTicketStatus() - ticketId={}, statusId={}", ticketId, statusId);

        Ticket ticket = ticketDAO.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        TicketStatus status = ticketStatusDAO.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Status not found"));

        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketDAO.update(ticket);

        logger.info("Ticket status updated - ticketId={}, newStatus={}", ticketId, status.getStatusName());
    }

    /**
     * Assign ticket to a technician.
     *
     * @param ticketId Ticket ID
     * @param technicianId Technician User ID
     */
    @Transactional
    public void assignTicket(Integer ticketId, Long technicianId) {
        logger.info("TicketService.assignTicket() - ticketId={}, technicianId={}", ticketId, technicianId);

        Ticket ticket = ticketDAO.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));

        User technician = userDAO.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found"));

        ticket.setAssignedTechnician(technician);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketDAO.update(ticket);

        logger.info("Ticket assigned - ticketId={}, technician={}", ticketId, technician.getEmail());
    }

    /**
     * Update ticket.
     *
     * @param ticket Ticket to update
     */
    @Transactional
    public void updateTicket(Ticket ticket) {
        logger.info("TicketService.updateTicket() - ticketId={}", ticket.getTicketId());
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketDAO.update(ticket);
    }

    /**
     * Delete ticket.
     *
     * @param ticketId Ticket ID
     */
    @Transactional
    public void deleteTicket(Integer ticketId) {
        logger.info("TicketService.deleteTicket() - ticketId={}", ticketId);
        Ticket ticket = ticketDAO.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticketDAO.delete(ticket);
    }

    /**
     * Get all categories.
     *
     * @return List of all categories
     */
    @Transactional(readOnly = true)
    public List<TicketCategory> getAllCategories() {
        logger.info("TicketService.getAllCategories()");
        return ticketCategoryDAO.findAll();
    }

    /**
     * Get all statuses.
     *
     * @return List of all statuses
     */
    @Transactional(readOnly = true)
    public List<TicketStatus> getAllStatuses() {
        logger.info("TicketService.getAllStatuses()");
        return ticketStatusDAO.findAll();
    }

    /**
     * Get ticket count for a user.
     *
     * @param userId User ID
     * @return Number of tickets
     */
    @Transactional(readOnly = true)
    public long getTicketCountByUserId(Long userId) {
        logger.info("TicketService.getTicketCountByUserId() - userId={}", userId);
        return ticketDAO.countByRequesterId(userId);
    }
}
