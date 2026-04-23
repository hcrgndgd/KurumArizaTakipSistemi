# Future Ticket Endpoints Implementation Guide

## Overview
This document provides code templates for implementing the ticket management endpoints that the profile page buttons will link to.

---

## 1. View Tickets List

### Endpoint Implementation (Add to UserController.java)

```java
/**
 * GET /user/tickets - Display user's tickets list
 * 
 * @param session HttpSession containing logged-in user
 * @param model Model to pass tickets to JSP view
 * @return "user-tickets-list" JSP view
 */
@GetMapping("/tickets")
public String viewTickets(HttpSession session, Model model) {
    logger.info("GET /user/tickets - View user tickets");
    
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        logger.warn("GET /user/tickets - No authenticated user. Redirecting to login.");
        return "redirect:/login";
    }
    
    // TODO: Implement TicketService.findTicketsByUser(userId)
    // List<Ticket> tickets = ticketService.findTicketsByUser(currentUser.getUserId());
    
    // model.addAttribute("tickets", tickets);
    logger.info("GET /user/tickets - Loaded tickets for user: {}", currentUser.getEmail());
    return "user-tickets-list";
}
```

### View File: `user-tickets-list.jsp`

```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Tickets</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h1>My Tickets</h1>
        <a href="<%= request.getContextPath() %>/user/profile" class="btn btn-secondary">Back to Profile</a>
    </div>

    <div class="tickets-list">
        <c:if test="${empty tickets}">
            <div class="empty-state">
                <p>You haven't created any tickets yet.</p>
                <a href="<%= request.getContextPath() %>/user/tickets/new" class="btn btn-primary">Create First Ticket</a>
            </div>
        </c:if>

        <c:if test="${not empty tickets}">
            <table class="tickets-table">
                <thead>
                    <tr>
                        <th>Ticket ID</th>
                        <th>Category</th>
                        <th>Status</th>
                        <th>Created Date</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="ticket" items="${tickets}">
                        <tr>
                            <td>#${ticket.ticketId}</td>
                            <td>${ticket.category.categoryName}</td>
                            <td><span class="badge badge-${ticket.status.statusName}">${ticket.status.statusName}</span></td>
                            <td>${ticket.createdAt}</td>
                            <td>
                                <a href="<%= request.getContextPath() %>/user/tickets/${ticket.ticketId}" class="btn btn-sm btn-primary">View</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</div>
</body>
</html>
```

---

## 2. Create New Ticket

### Step 1: Show Form (Add to UserController.java)

```java
/**
 * GET /user/tickets/new - Display create ticket form
 * 
 * @param session HttpSession containing logged-in user
 * @param model Model to pass ticket categories to JSP view
 * @return "user-ticket-new" JSP view
 */
@GetMapping("/tickets/new")
public String showNewTicketForm(HttpSession session, Model model) {
    logger.info("GET /user/tickets/new - Show create ticket form");
    
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        logger.warn("GET /user/tickets/new - No authenticated user. Redirecting to login.");
        return "redirect:/login";
    }
    
    // TODO: Implement TicketService.getAllCategories()
    // List<TicketCategory> categories = ticketService.getAllCategories();
    
    // model.addAttribute("categories", categories);
    logger.info("GET /user/tickets/new - Form loaded for user: {}", currentUser.getEmail());
    return "user-ticket-new";
}
```

### Step 2: Save Ticket (Add to UserController.java)

```java
/**
 * POST /user/tickets - Create new ticket
 * 
 * @param session HttpSession containing logged-in user
 * @param ticketData Map containing ticket information
 * @return JSON response with result
 */
@PostMapping("/tickets")
public ResponseEntity<Map<String, Object>> createTicket(
        HttpSession session,
        @RequestBody Map<String, Object> ticketData) {
    
    logger.info("POST /user/tickets - Create new ticket");
    
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        logger.warn("POST /user/tickets - No authenticated user. Returning 401.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "Not authenticated"));
    }
    
    try {
        // TODO: Implement ticket creation logic
        // String title = (String) ticketData.get("title");
        // String description = (String) ticketData.get("description");
        // Long categoryId = Long.parseLong(ticketData.get("categoryId").toString());
        
        // Ticket ticket = ticketService.createTicket(currentUser.getUserId(), title, description, categoryId);
        
        logger.info("POST /user/tickets - Ticket created: {}", "ticketId");
        return ResponseEntity.ok(Map.of(
                "message", "Ticket created successfully",
                "ticketId", "ticketId"
        ));
    } catch (Exception e) {
        logger.error("POST /user/tickets - Error creating ticket", e);
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Failed to create ticket: " + e.getMessage()));
    }
}
```

### View File: `user-ticket-new.jsp`

```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Ticket</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h1>Create New Ticket</h1>
    </div>

    <form id="ticketForm" class="ticket-form">
        <div class="form-group">
            <label for="title">Title *</label>
            <input type="text" id="title" name="title" required placeholder="Brief description of the issue">
        </div>

        <div class="form-group">
            <label for="category">Category *</label>
            <select id="category" name="categoryId" required>
                <option value="">Select a category</option>
                <c:forEach var="category" items="${categories}">
                    <option value="${category.categoryId}">${category.categoryName}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label for="description">Description *</label>
            <textarea id="description" name="description" required rows="5" placeholder="Provide detailed information about your issue"></textarea>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn btn-primary">Create Ticket</button>
            <a href="<%= request.getContextPath() %>/user/tickets" class="btn btn-secondary">Cancel</a>
        </div>

        <div id="feedback" class="feedback" aria-live="polite"></div>
    </form>
</div>

<script>
    const form = document.getElementById("ticketForm");
    const feedback = document.getElementById("feedback");

    function setFeedback(message, type) {
        feedback.textContent = message;
        feedback.className = "feedback " + type;
    }

    form.addEventListener("submit", async function (event) {
        event.preventDefault();
        
        const payload = {
            title: form.title.value.trim(),
            description: form.description.value.trim(),
            categoryId: form.categoryId.value
        };

        try {
            const response = await fetch("<%= request.getContextPath() %>/user/tickets", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (!response.ok) {
                setFeedback(data.error || "Failed to create ticket", "error");
                return;
            }

            setFeedback("Ticket created successfully! Redirecting...", "success");
            
            setTimeout(() => {
                window.location.href = "<%= request.getContextPath() %>/user/tickets/" + data.ticketId;
            }, 1500);
        } catch (error) {
            setFeedback("Server error. Please try again later.", "error");
        }
    });
</script>
</body>
</html>
```

---

## 3. View Ticket Details

### Endpoint Implementation (Add to UserController.java)

```java
/**
 * GET /user/tickets/{id} - Display ticket details
 * 
 * @param id Ticket ID
 * @param session HttpSession containing logged-in user
 * @param model Model to pass ticket to JSP view
 * @return "user-ticket-detail" JSP view
 */
@GetMapping("/tickets/{id}")
public String viewTicketDetail(
        @PathVariable Long id,
        HttpSession session,
        Model model) {
    
    logger.info("GET /user/tickets/{} - View ticket detail", id);
    
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        logger.warn("GET /user/tickets/{} - No authenticated user. Redirecting to login.", id);
        return "redirect:/login";
    }
    
    try {
        // TODO: Implement TicketService.getTicketDetail(id, userId)
        // Ticket ticket = ticketService.getTicketDetail(id, currentUser.getUserId());
        
        // model.addAttribute("ticket", ticket);
        logger.info("GET /user/tickets/{} - Ticket loaded for user: {}", id, currentUser.getEmail());
        return "user-ticket-detail";
    } catch (Exception e) {
        logger.error("GET /user/tickets/{} - Error loading ticket", id, e);
        return "redirect:/user/tickets";
    }
}
```

### View File: `user-ticket-detail.jsp`

```jsp
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ticket #${ticket.ticketId}</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
<div class="container">
    <div class="header">
        <h1>Ticket #${ticket.ticketId}</h1>
        <a href="<%= request.getContextPath() %>/user/tickets" class="btn btn-secondary">Back to Tickets</a>
    </div>

    <div class="ticket-detail">
        <h2>${ticket.title}</h2>
        
        <div class="ticket-meta">
            <p><strong>Status:</strong> <span class="badge badge-${ticket.status.statusName}">${ticket.status.statusName}</span></p>
            <p><strong>Category:</strong> ${ticket.category.categoryName}</p>
            <p><strong>Created:</strong> ${ticket.createdAt}</p>
        </div>

        <div class="ticket-description">
            <h3>Description</h3>
            <p>${ticket.description}</p>
        </div>

        <div class="ticket-comments">
            <h3>Comments</h3>
            <!-- TODO: Display ticket comments/responses -->
        </div>
    </div>
</div>
</body>
</html>
```

---

## 4. Required Service Methods

Add these to `TicketService.java`:

```java
// Get all ticket categories
public List<TicketCategory> getAllCategories() {
    logger.info("TicketService.getAllCategories()");
    return ticketCategoryDAO.findAll();
}

// Find tickets by user
public List<Ticket> findTicketsByUser(Long userId) {
    logger.info("TicketService.findTicketsByUser() - userId={}", userId);
    return ticketDAO.findByUserId(userId);
}

// Create new ticket
@Transactional
public Ticket createTicket(Long userId, String title, String description, Long categoryId) {
    logger.info("TicketService.createTicket() - userId={}", userId);
    
    User user = userDAO.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
    TicketCategory category = ticketCategoryDAO.findById(categoryId)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    
    TicketStatus openStatus = ticketStatusDAO.findByName("OPEN")
            .orElseThrow(() -> new IllegalArgumentException("Status not found"));
    
    Ticket ticket = new Ticket();
    ticket.setUser(user);
    ticket.setTitle(title);
    ticket.setDescription(description);
    ticket.setCategory(category);
    ticket.setStatus(openStatus);
    ticket.setCreatedAt(LocalDateTime.now());
    
    return ticketDAO.save(ticket);
}

// Get ticket detail (with validation for ownership)
public Ticket getTicketDetail(Long ticketId, Long userId) {
    logger.info("TicketService.getTicketDetail() - ticketId={}, userId={}", ticketId, userId);
    
    Ticket ticket = ticketDAO.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
    
    // Verify ticket belongs to user
    if (!ticket.getUser().getUserId().equals(userId)) {
        throw new IllegalArgumentException("Access denied");
    }
    
    return ticket;
}
```

---

## 5. Required DAO Methods

Add these to `TicketDAO.java`:

```java
// Find tickets by user ID
public List<Ticket> findByUserId(Long userId) {
    String query = "FROM Ticket WHERE user.userId = :userId ORDER BY createdAt DESC";
    return sessionFactory.getCurrentSession()
            .createQuery(query, Ticket.class)
            .setParameter("userId", userId)
            .getResultList();
}
```

---

## 6. JSP View Folder Structure

```
webapp/
├── WEB-INF/
    └── view/
        ├── user-profile.jsp (already created ✓)
        ├── user-tickets-list.jsp (to create)
        ├── user-ticket-new.jsp (to create)
        └── user-ticket-detail.jsp (to create)
```

---

## 7. Integration Checklist

- [ ] Add `TicketService` autowiring to `UserController`
- [ ] Implement `getAllCategories()` in `TicketService`
- [ ] Implement `findTicketsByUser()` in `TicketService`
- [ ] Implement `createTicket()` in `TicketService`
- [ ] Implement `getTicketDetail()` in `TicketService`
- [ ] Add `findByUserId()` to `TicketDAO`
- [ ] Create `user-tickets-list.jsp`
- [ ] Create `user-ticket-new.jsp`
- [ ] Create `user-ticket-detail.jsp`
- [ ] Add three new methods to `UserController`
- [ ] Test all endpoints with authentication
- [ ] Verify session handling and security
