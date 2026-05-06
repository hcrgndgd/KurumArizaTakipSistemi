package com.JavaProje.KurumArizaTakipSistemi.web;

import com.JavaProje.KurumArizaTakipSistemi.model.Ticket;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import com.JavaProje.KurumArizaTakipSistemi.service.CategorySuggestionService;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

/**
 * UserController handles user-related views and operations.
 * Routes: /user/*, /tickets/*
 */
@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private TicketService ticketService;

    @Autowired
    private CategorySuggestionService categorySuggestionService;

    /**
     * GET /user/profile - Display user profile page
     */
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        logger.info("GET /user/profile - User profile request");

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            logger.warn("GET /user/profile - No authenticated user in session. Redirecting to login.");
            return "redirect:/login";
        }

        logger.info("GET /user/profile - Profile loaded for user: {}", currentUser.getEmail());
        model.addAttribute("user", currentUser);
        return "user-profile";
    }

    /**
     * GET /user/tickets - Display list of tickets created by current user
     */
    @GetMapping("/tickets")
    public String listTickets(HttpSession session, Model model) {
        logger.info("GET /user/tickets - List user tickets");

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            logger.warn("GET /user/tickets - No authenticated user. Redirecting to login.");
            return "redirect:/login";
        }

        try {
            List<Ticket> tickets = ticketService.getTicketsByRequesterId(currentUser.getUserId());
            logger.info("GET /user/tickets - Loaded {} tickets for user: {}", tickets.size(), currentUser.getEmail());
            model.addAttribute("tickets", tickets);
            model.addAttribute("locale", LocaleContextHolder.getLocale());
        } catch (Exception e) {
            logger.error("GET /user/tickets - Error loading tickets", e);
            model.addAttribute("error", "Error loading tickets: " + e.getMessage());
        }

        return "tickets/liste";
    }

    /**
     * GET /user/tickets/new - Display create new ticket form
     */
    @GetMapping("/tickets/new")
    public String showNewTicketForm(HttpSession session, Model model) {
        logger.info("GET /user/tickets/new - Show create ticket form");

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            logger.warn("GET /user/tickets/new - No authenticated user. Redirecting to login.");
            return "redirect:/login";
        }

        try {
            model.addAttribute("categories", ticketService.getAllCategories());
            model.addAttribute("locale", LocaleContextHolder.getLocale());
            logger.info("GET /user/tickets/new - Form loaded for user: {}", currentUser.getEmail());
        } catch (Exception e) {
            logger.error("GET /user/tickets/new - Error loading categories", e);
            model.addAttribute("error", "Error loading categories: " + e.getMessage());
        }

        return "tickets/new";
    }

    /**
     * POST /user/tickets - Create a new ticket
     */
    @PostMapping("/tickets")
    public String createTicket(
            HttpSession session,
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            Model model) {

        logger.info("POST /user/tickets - Create new ticket - title={}", title);

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            logger.warn("POST /user/tickets - No authenticated user. Redirecting to login.");
            return "redirect:/login";
        }

        try {
            if (categoryId == null) {
                categoryId = categorySuggestionService.resolveCategory(null, title, description)
                        .map(tc -> tc.getCategoryId())
                        .orElse(null);
                logger.info("POST /user/tickets - Category determined by Gemini AI: {}", categoryId);
            }
            Ticket ticket = ticketService.createTicket(
                    currentUser.getUserId(),
                    title,
                    description,
                    categoryId
            );
            logger.info("POST /user/tickets - Ticket created successfully - ticketId={}", ticket.getTicketId());
            return "redirect:/user/tickets";
        } catch (IllegalArgumentException e) {
            logger.warn("POST /user/tickets - Validation error: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("categories", ticketService.getAllCategories());
            model.addAttribute("locale", LocaleContextHolder.getLocale());
            model.addAttribute("title", title);
            model.addAttribute("description", description);
            return "tickets/new";
        } catch (Exception e) {
            logger.error("POST /user/tickets - Error creating ticket", e);
            model.addAttribute("error", "Error creating ticket: " + e.getMessage());
            model.addAttribute("categories", ticketService.getAllCategories());
            model.addAttribute("locale", LocaleContextHolder.getLocale());
            return "tickets/new";
        }
    }

    /**
     * GET /user/tickets/{id} - Display ticket details
     */
    @GetMapping("/tickets/{id}")
    public String viewTicketDetail(
            @PathVariable Integer id,
            HttpSession session,
            Model model) {

        logger.info("GET /user/tickets/{} - View ticket detail", id);

        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            logger.warn("GET /user/tickets/{} - No authenticated user. Redirecting to login.", id);
            return "redirect:/login";
        }

        try {
            Ticket ticket = ticketService.getTicketById(id);

            if (ticket.getRequester() == null || !ticket.getRequester().getUserId().equals(currentUser.getUserId())) {
                logger.warn("GET /user/tickets/{} - Access denied for user: {}", id, currentUser.getEmail());
                return "redirect:/user/tickets";
            }

            model.addAttribute("ticket", ticket);
            model.addAttribute("locale", LocaleContextHolder.getLocale());
            logger.info("GET /user/tickets/{} - Ticket loaded for user: {}", id, currentUser.getEmail());
            return "tickets/detail";
        } catch (IllegalArgumentException e) {
            logger.warn("GET /user/tickets/{} - Ticket not found", id);
            return "redirect:/user/tickets";
        } catch (Exception e) {
            logger.error("GET /user/tickets/{} - Error loading ticket", id, e);
            model.addAttribute("error", "Error loading ticket: " + e.getMessage());
            return "redirect:/user/tickets";
        }
    }
}