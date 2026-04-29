package com.JavaProje.KurumArizaTakipSistemi.web;

import com.JavaProje.KurumArizaTakipSistemi.model.Ticket;
import com.JavaProje.KurumArizaTakipSistemi.model.TicketStatus;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import com.JavaProje.KurumArizaTakipSistemi.service.TicketService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
        logger.info("GET /technician/tickets - Bekleyen ticketlar listeleniyor");

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("GET /technician/tickets - Yetkisiz erişim");
            return "redirect:/login";
        }

        try {
            List<Ticket> allTickets = ticketService.getAllTickets();
            List<Ticket> availableTickets = allTickets.stream()
                    .filter(t -> t.getAssignedTechnician() == null)
                    .toList();

            model.addAttribute("tickets", availableTickets);
            model.addAttribute("technician", technician);
            logger.info("GET /technician/tickets - {} atanmamış ticket bulundu", availableTickets.size());
        } catch (Exception e) {
            logger.error("GET /technician/tickets - Hata: {}", e.getMessage());
            model.addAttribute("error", "Ticketlar yüklenirken hata oluştu.");
        }

        return "technician/tickets";
    }

    @GetMapping("/my-tickets")
    public String listMyTickets(HttpSession session, Model model) {
        logger.info("GET /technician/my-tickets - Teknisyene atanmış ticketlar listeleniyor");

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("GET /technician/my-tickets - Yetkisiz erişim");
            return "redirect:/login";
        }

        try {
            List<Ticket> myTickets = ticketService.getTicketsByAssignedTechnician(technician.getUserId());
            model.addAttribute("tickets", myTickets);
            model.addAttribute("technician", technician);
            model.addAttribute("statuses", ticketService.getAllStatuses());
            logger.info("GET /technician/my-tickets - {} ticket bulundu | technician={}",
                    myTickets.size(), technician.getEmail());
        } catch (Exception e) {
            logger.error("GET /technician/my-tickets - Hata: {}", e.getMessage());
            model.addAttribute("error", "Ticketlar yüklenirken hata oluştu.");
        }

        return "technician/my-tickets";
    }

    @PostMapping("/tickets/{id}/assign")
    public String assignTicket(@PathVariable("id") Integer id, HttpSession session) {
        logger.info("POST /technician/tickets/{}/assign - Ticket alınıyor", id);

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("POST /technician/tickets/{}/assign - Yetkisiz erişim", id);
            return "redirect:/login";
        }

        try {
            Ticket ticket = ticketService.getTicketById(id);

            if (ticket.getAssignedTechnician() != null) {
                logger.warn("POST /technician/tickets/{}/assign - Ticket zaten atanmış", id);
                return "redirect:/technician/tickets?error=already_assigned";
            }

            ticketService.assignTicket(id, technician.getUserId());

            List<TicketStatus> statuses = ticketService.getAllStatuses();
            statuses.stream()
                    .filter(s -> s.getStatusName().equals("BAŞLADI"))
                    .findFirst()
                    .ifPresent(s -> ticketService.updateTicketStatus(id, s.getStatusId()));

            logger.info("POST /technician/tickets/{}/assign - Ticket alındı | technician={}",
                    id, technician.getEmail());
        } catch (Exception e) {
            logger.error("POST /technician/tickets/{}/assign - Hata: {}", id, e.getMessage());
            return "redirect:/technician/tickets?error=true";
        }

        return "redirect:/technician/my-tickets";
    }

    @PostMapping("/tickets/{id}/status")
    public String updateStatus(
            @PathVariable("id") Integer id,
            @RequestParam("statusId") Integer statusId,
            HttpSession session) {

        logger.info("POST /technician/tickets/{}/status - Durum güncelleniyor | statusId={}", id, statusId);

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("POST /technician/tickets/{}/status - Yetkisiz erişim", id);
            return "redirect:/login";
        }

        try {
            Ticket ticket = ticketService.getTicketById(id);

            if (ticket.getAssignedTechnician() == null ||
                    !ticket.getAssignedTechnician().getUserId().equals(technician.getUserId())) {
                logger.warn("POST /technician/tickets/{}/status - Bu ticket teknisyene ait değil", id);
                return "redirect:/technician/my-tickets?error=not_yours";
            }

            ticketService.updateTicketStatus(id, statusId);
            logger.info("POST /technician/tickets/{}/status - Durum güncellendi | technician={}",
                    id, technician.getEmail());
        } catch (Exception e) {
            logger.error("POST /technician/tickets/{}/status - Hata: {}", id, e.getMessage());
            return "redirect:/technician/my-tickets?error=true";
        }

        return "redirect:/technician/my-tickets";
    }

    @PostMapping("/tickets/{id}/unassign")
    public String unassignTicket(@PathVariable("id") Integer id, HttpSession session) {
        logger.info("POST /technician/tickets/{}/unassign - Ticket bırakılıyor", id);

        User technician = getAuthenticatedTechnician(session);
        if (technician == null) {
            logger.warn("POST /technician/tickets/{}/unassign - Yetkisiz erişim", id);
            return "redirect:/login";
        }

        try {
            Ticket ticket = ticketService.getTicketById(id);

            if (ticket.getAssignedTechnician() == null ||
                    !ticket.getAssignedTechnician().getUserId().equals(technician.getUserId())) {
                logger.warn("POST /technician/tickets/{}/unassign - Bu ticket teknisyene ait değil", id);
                return "redirect:/technician/my-tickets?error=not_yours";
            }

            ticket.setAssignedTechnician(null);
            ticketService.updateTicket(ticket);

            List<TicketStatus> statuses = ticketService.getAllStatuses();
            statuses.stream()
                    .filter(s -> s.getStatusName().equals("BEKLEMEDE"))
                    .findFirst()
                    .ifPresent(s -> ticketService.updateTicketStatus(id, s.getStatusId()));

            logger.info("POST /technician/tickets/{}/unassign - Ticket bırakıldı | technician={}",
                    id, technician.getEmail());
        } catch (Exception e) {
            logger.error("POST /technician/tickets/{}/unassign - Hata: {}", id, e.getMessage());
            return "redirect:/technician/my-tickets?error=true";
        }

        return "redirect:/technician/tickets";
    }
}