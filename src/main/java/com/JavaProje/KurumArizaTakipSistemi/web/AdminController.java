package com.JavaProje.KurumArizaTakipSistemi.web;

import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import com.JavaProje.KurumArizaTakipSistemi.model.Ticket;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
import com.JavaProje.KurumArizaTakipSistemi.service.TicketService;
import com.JavaProje.KurumArizaTakipSistemi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private TicketService ticketService;

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> loadUsers()
    {
        List<Map<String, Object>> users = userService.loadAllUsers().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("id") long id)
    {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("DELETE /admin/users/{} failed", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    @GetMapping("/tickets/active")
    public ResponseEntity<List<Map<String, Object>>> loadActiveTickets() {
        List<Map<String, Object>> tickets = ticketService.getAllTickets().stream()
                .filter(this::isActiveTicket)
                .map(this::toTicketResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/tickets/{ticketId}/assignee/{userId}")
    public ResponseEntity<Map<String, String>> assignTicket(
            @PathVariable("ticketId") Integer ticketId,
            @PathVariable("userId") long userId) {
        try {
            Ticket ticket = getTicketFromAllTickets(ticketId);
            if (!isActiveTicket(ticket)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only active tickets can be assigned."));
            }

            User assignee = getTechnicians().stream()
                    .filter(user -> user.getUserId().equals(userId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Only technicians can be assigned to tickets."));
            ticketService.assignTicket(ticketId, assignee.getUserId());
            return ResponseEntity.ok(Map.of("message", "Ticket assigned successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("PUT /admin/tickets/{}/assignee/{} failed", ticketId, userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    @DeleteMapping("/tickets/{ticketId}/assignee")
    public ResponseEntity<Map<String, String>> unassignTicket(@PathVariable("ticketId") Integer ticketId) {
        try {
            Ticket ticket = getTicketFromAllTickets(ticketId);
            if (!isActiveTicket(ticket)) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only active tickets can be unassigned."));
            }

            ticketService.unassignTicket(ticketId);
            return ResponseEntity.ok(Map.of("message", "Ticket unassigned successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("DELETE /admin/tickets/{}/assignee failed", ticketId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    @PutMapping("/users/{userId}/role/{roleId}")
    public ResponseEntity<Map<String, String>> setRole(@PathVariable("userId") long userId, @PathVariable("roleId") long roleId)
    {
        try {
            userService.setRole(userId, roleId);
            return ResponseEntity.ok(Map.of("message", "Role updated successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("PUT /admin/users/{}/role/{} failed", userId, roleId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    @PostMapping("/roles")
    public ResponseEntity<Map<String, String>> addRoles(@RequestBody Map<String, String> body)
    {
        try {
            userService.addRole(body.get("role"));
            return ResponseEntity.ok(Map.of("message", "Role added successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/roles")
    public ResponseEntity<Map<String, String>> deleteRoles(@RequestBody Role role)
    {
        try {
            userService.deleteRole(role != null ? role.getRoleName() : null);
            return ResponseEntity.ok(Map.of("message", "Role deleted successfully."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Map<String, Object>>> loadRoles() {
        Map<String, Long> userCountsByRole = userService.loadAllUsers().stream()
                .filter(user -> user.getRole() != null && user.getRole().getRoleName() != null)
                .collect(Collectors.groupingBy(user -> user.getRole().getRoleName(), Collectors.counting()));

        List<Map<String, Object>> roles = userService.loadAllRoles().stream()
                .map(role -> toRoleResponse(role, userCountsByRole.getOrDefault(role.getRoleName(), 0L)))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    private Map<String, Object> toUserResponse(User user) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("userId", user.getUserId());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("verified", user.getVerified());
        response.put("roleId", user.getRole() != null ? user.getRole().getRoleId() : null);
        response.put("roleName", user.getRole() != null ? user.getRole().getRoleName() : null);
        return response;
    }

    private Map<String, Object> toRoleResponse(Role role, long userCount) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("roleId", role.getRoleId());
        response.put("roleName", role.getRoleName());
        response.put("userCount", userCount);
        return response;
    }

    private Map<String, Object> toTicketResponse(Ticket ticket) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("ticketId", ticket.getTicketId());
        response.put("title", ticket.getTitle());
        response.put("description", ticket.getDescription());
        response.put("statusId", ticket.getStatus() != null ? ticket.getStatus().getStatusId() : null);
        response.put("statusName", ticket.getStatus() != null ? ticket.getStatus().getStatusName() : null);
        response.put("categoryId", ticket.getCategory() != null ? ticket.getCategory().getCategoryId() : null);
        response.put("categoryName", ticket.getCategory() != null ? ticket.getCategory().getCategoryName() : null);
        response.put("requester", ticket.getRequester() != null ? toUserResponse(ticket.getRequester()) : null);
        response.put("assignedUser", ticket.getAssignedTechnician() != null
                ? toUserResponse(ticket.getAssignedTechnician())
                : null);
        response.put("createdAt", ticket.getCreatedAt() != null ? ticket.getCreatedAt().toString() : null);
        response.put("updatedAt", ticket.getUpdatedAt() != null ? ticket.getUpdatedAt().toString() : null);
        return response;
    }

    private Ticket getTicketFromAllTickets(Integer ticketId) {
        return ticketService.getAllTickets().stream()
                .filter(ticket -> ticket.getTicketId().equals(ticketId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    }

    private List<User> getTechnicians() {
        return userService.loadAllUsers().stream()
                .filter(user -> user.getRole() != null
                        && user.getRole().getRoleName() != null
                        && "TECHNICIAN".equalsIgnoreCase(user.getRole().getRoleName().trim()))
                .collect(Collectors.toList());
    }

    private boolean isActiveTicket(Ticket ticket) {
        if (ticket == null || ticket.getStatus() == null || ticket.getStatus().getStatusName() == null) {
            return false;
        }

        String statusName = ticket.getStatus().getStatusName().trim().toUpperCase();
        return !List.of("CLOSED", "COMPLETED", "RESOLVED", "KAPALI", "TAMAMLANDI").contains(statusName);
    }
}
