package com.JavaProje.KurumArizaTakipSistemi.web;

import com.JavaProje.KurumArizaTakipSistemi.model.Ticket;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import com.JavaProje.KurumArizaTakipSistemi.service.TicketService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    private static final Logger logger = LoggerFactory.getLogger(TechnicianController.class);

    @Autowired
    private TicketService ticketService;

    private User getAuthenticatedTechnician(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return null;
        String role = (String) session.getAttribute("userRole");
        if (!"TECHNICIAN".equals(role)) return null;
        return currentUser;
    }

    @GetMapping("/tickets")
    public String listAvailableTickets(HttpSession session, Model model) {
        logger.info("GET /technician/tickets - listing available tickets");

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("GET /technician/tickets - unauthorized access");
            return "redirect:/login";
        }

        try {
            List<Ticket> allTickets = ticketService.getAllTickets();
            List<Ticket> availableTickets = allTickets.stream()
                    .filter(t -> t.getAssignedTechnician() == null)
                    .toList();

            model.addAttribute("tickets", availableTickets);
            model.addAttribute("technician", technician);
            model.addAttribute("locale", LocaleContextHolder.getLocale());
            logger.info("GET /technician/tickets - {} unassigned tickets found", availableTickets.size());
        } catch (Exception e) {
            logger.error("GET /technician/tickets - failed", e);
            model.addAttribute("error", "Ticketlar yuklenirken hata olustu.");
        }

        return "technician/tickets";
    }

    @GetMapping("/my-tickets")
    public String listMyTickets(HttpSession session, Model model) {
        logger.info("GET /technician/my-tickets - listing assigned tickets");

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("GET /technician/my-tickets - unauthorized access");
            return "redirect:/login";
        }

        try {
            List<Ticket> myTickets = ticketService.getTicketsByAssignedTechnician(technician.getUserId());
            List<Ticket> activeTickets = myTickets.stream()
                    .filter(ticket -> !isClosed(ticket))
                    .toList();
            List<Ticket> completedTickets = myTickets.stream()
                    .filter(this::isClosed)
                    .toList();

            model.addAttribute("tickets", activeTickets);
            model.addAttribute("completedTickets", completedTickets);
            model.addAttribute("technician", technician);
            model.addAttribute("locale", LocaleContextHolder.getLocale());
            logger.info("GET /technician/my-tickets - {} active, {} completed | technician={}",
                    activeTickets.size(), completedTickets.size(), technician.getEmail());
        } catch (Exception e) {
            logger.error("GET /technician/my-tickets - failed", e);
            model.addAttribute("error", "Ticketlar yuklenirken hata olustu.");
        }

        return "technician/my-tickets";
    }

    @PostMapping("/tickets/{id}/assign")
    public String assignTicket(@PathVariable("id") Integer id, HttpSession session) {
        logger.info("POST /technician/tickets/{}/assign - assigning ticket", id);

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("POST /technician/tickets/{}/assign - unauthorized access", id);
            return "redirect:/login";
        }

        try {
            Ticket ticket = ticketService.getTicketById(id);

            if (ticket.getAssignedTechnician() != null) {
                logger.warn("POST /technician/tickets/{}/assign - ticket already assigned", id);
                return "redirect:/technician/tickets?error=already_assigned";
            }

            ticketService.assignTicket(id, technician.getUserId());
            logger.info("POST /technician/tickets/{}/assign - ticket assigned | technician={}",
                    id, technician.getEmail());
        } catch (Exception e) {
            logger.error("POST /technician/tickets/{}/assign - failed", id, e);
            return "redirect:/technician/tickets?error=true";
        }

        return "redirect:/technician/my-tickets";
    }

    @PostMapping("/tickets/{id}/finish")
    public String finishTicket(@PathVariable("id") Integer id, HttpSession session) {
        logger.info("POST /technician/tickets/{}/finish - closing ticket", id);

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("POST /technician/tickets/{}/finish - unauthorized access", id);
            return "redirect:/login";
        }

        try {
            Ticket ticket = ticketService.getTicketById(id);

            if (ticket.getAssignedTechnician() == null ||
                    !ticket.getAssignedTechnician().getUserId().equals(technician.getUserId())) {
                logger.warn("POST /technician/tickets/{}/finish - ticket does not belong to technician", id);
                return "redirect:/technician/my-tickets?error=not_yours";
            }
            if (isClosed(ticket)) {
                logger.warn("POST /technician/tickets/{}/finish - closed ticket cannot be edited", id);
                return "redirect:/technician/my-tickets?error=closed";
            }

            ticketService.closeTicket(id);
            logger.info("POST /technician/tickets/{}/finish - ticket closed | technician={}",
                    id, technician.getEmail());
        } catch (Exception e) {
            logger.error("POST /technician/tickets/{}/finish - failed", id, e);
            return "redirect:/technician/my-tickets?error=true";
        }

        return "redirect:/technician/my-tickets";
    }

    @PostMapping("/tickets/{id}/unassign")
    public String unassignTicket(@PathVariable("id") Integer id, HttpSession session) {
        logger.info("POST /technician/tickets/{}/unassign - unassigning ticket", id);

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("POST /technician/tickets/{}/unassign - unauthorized access", id);
            return "redirect:/login";
        }

        try {
            Ticket ticket = ticketService.getTicketById(id);

            if (ticket.getAssignedTechnician() == null ||
                    !ticket.getAssignedTechnician().getUserId().equals(technician.getUserId())) {
                logger.warn("POST /technician/tickets/{}/unassign - ticket does not belong to technician", id);
                return "redirect:/technician/my-tickets?error=not_yours";
            }
            if (isClosed(ticket)) {
                logger.warn("POST /technician/tickets/{}/unassign - closed ticket cannot be unassigned", id);
                return "redirect:/technician/my-tickets?error=closed";
            }

            ticketService.unassignTicket(id);
            logger.info("POST /technician/tickets/{}/unassign - ticket unassigned | technician={}",
                    id, technician.getEmail());
        } catch (Exception e) {
            logger.error("POST /technician/tickets/{}/unassign - failed", id, e);
            return "redirect:/technician/my-tickets?error=true";
        }

        return "redirect:/technician/tickets";
    }

    private boolean isClosed(Ticket ticket) {
        return ticket != null
                && ticket.getStatus() != null
                && ticket.getStatus().getStatusName() != null
                && "Kapalı".equalsIgnoreCase(ticket.getStatus().getStatusName().trim());
    }
}