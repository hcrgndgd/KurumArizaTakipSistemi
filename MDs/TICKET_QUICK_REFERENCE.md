# 🎫 Ticket System - Quick Reference

## 🚀 Quick Start

### File Structure
```
src/main/
├── java/com/JavaProje/KurumArizaTakipSistemi/
│   ├── dao/
│   │   ├── TicketDAO.java          ✅ NEW
│   │   ├── TicketCategoryDAO.java  ✅ NEW
│   │   └── TicketStatusDAO.java    ✅ NEW
│   ├── service/
│   │   └── TicketService.java      ✅ NEW
│   └── web/
│       └── UserController.java     ✅ UPDATED (6 new methods)
└── webapp/WEB-INF/view/
    └── tickets/
        ├── liste.jsp               ✅ UPDATED (styled)
        ├── new.jsp                 ✅ UPDATED (styled)
        └── detail.jsp              ✅ NEW
```

---

## 📡 API Endpoints

### Public Routes (Requires Authentication)
```
GET     /user/tickets              → List user's tickets
GET     /user/tickets/new          → Show create form
POST    /user/tickets              → Create ticket
GET     /user/tickets/{id}         → View ticket details
```

### From Profile Page
```
User Profile
├─ "Create Ticket" button → /user/tickets/new
└─ "View Tickets" button → /user/tickets
```

---

## 🔄 Flow Diagram

```
┌─────────────────────────────────┐
│   User Profile                  │
│   ├─ Create Ticket              │
│   └─ View Tickets               │
└────────┬────────────────────────┘
         │
    ┌────┴──────────────────┐
    │                       │
    ▼                       ▼
Ticket List             Ticket Form
├─ Empty state          ├─ Categories
├─ Table of tickets     ├─ Title input
├─ Click to view        ├─ Description input
└─ Create button        └─ Submit/Cancel
    │                       │
    └───────────┬───────────┘
                │
                ▼
         Ticket Detail
         ├─ Full title
         ├─ Full description
         ├─ Status badge
         ├─ Category
         ├─ Dates
         └─ Back button
```

---

## 💾 Database Tables

### tickets
| Column | Type | Notes |
|--------|------|-------|
| TicketId | INT (PK) | Auto-increment |
| Title | VARCHAR(200) | Required |
| Description | TEXT | Required |
| RequesterId | BIGINT (FK) | From users.user_id |
| AssignedTechnicianId | BIGINT (FK) | Nullable |
| StatusId | INT (FK) | From ticket_statuses |
| CategoryId | INT (FK) | From ticket_categories |
| CreatedAt | DATETIME | Set automatically |
| UpdatedAt | DATETIME | Updated automatically |

### ticket_categories
| Column | Type |
|--------|------|
| CategoryId | INT (PK) |
| CategoryName | VARCHAR(100) |

### ticket_statuses
| Column | Type |
|--------|------|
| StatusId | INT (PK) |
| StatusName | VARCHAR(50) |

---

## 🎯 Key Methods

### TicketDAO
```java
save(Ticket)                              // Save new ticket
findById(Integer id)                      // Get by ID
findByRequesterId(Long userId)            // User's tickets
findByAssignedTechnicianId(Long id)      // Assigned tickets
findAll()                                 // All tickets
countByRequesterId(Long userId)           // Count user's tickets
```

### TicketService
```java
createTicket(userId, title, desc, catId) // Create with validation
getTicketsByRequesterId(userId)           // User's tickets
getTicketById(id)                         // Get single ticket
getAllCategories()                        // For dropdown
getAllStatuses()                          // For status list
updateTicketStatus(id, statusId)          // Change status
assignTicket(id, technicianId)           // Assign to tech
```

### UserController
```java
@GetMapping("/tickets")                   // List page
@GetMapping("/tickets/new")               // Create form
@PostMapping("/tickets")                  // Create ticket
@GetMapping("/tickets/{id}")              // Detail page
```

---

## 🧪 Testing Endpoints

### Test Create
```bash
POST /user/tickets
Content-Type: application/x-www-form-urlencoded

title=Test%20Ticket
description=Test%20Description
categoryId=1
```

### Test List
```bash
GET /user/tickets
```

### Test Detail
```bash
GET /user/tickets/1
```

---

## ✅ Validation Rules

### Create Ticket
- Title: Required, max 200 chars
- Description: Required, max 2000 chars
- Category: Must exist in DB
- User: Must be logged in (session)

### Form Submission
- All fields required
- Category dropdown must select valid option
- Errors displayed on form

---

## 🎨 UI Components

### Liste.jsp
- **Header**: "🎫 Ticket Listesi" with "Yeni Ticket" button
- **Table**: ID, Title, Category, Status, Date columns
- **Status Badge**: Color-coded (Open, In Progress, Closed)
- **Empty State**: Message + create button
- **Styling**: Modern, responsive, mobile-friendly

### New.jsp
- **Header**: "📝 Yeni Ticket"
- **Form**:
  - Title field (200 char max)
  - Category dropdown
  - Description textarea (2000 char max)
  - Gönder / İptal buttons
- **Info**: Message about auto-categorization
- **Styling**: Clean, accessible, mobile-friendly

### Detail.jsp
- **Header**: "🎫 Ticket #" with back button
- **Content**:
  - Title section
  - Description section
  - Meta grid:
    - Status badge
    - Category name
    - Created date
    - Updated date
    - Assigned technician
- **Styling**: Clean layout, mobile-friendly

---

## 🔐 Security Features

### Session Validation
```java
User currentUser = (User) session.getAttribute("currentUser");
if (currentUser == null) {
    return "redirect:/login";
}
```

### Ownership Check
```java
if (!ticket.getRequester().getUserId().equals(currentUser.getUserId())) {
    return "redirect:/user/tickets";
}
```

### Input Validation
```java
if (title == null || title.trim().isEmpty()) {
    throw new IllegalArgumentException("Title required");
}
```

---

## 🔍 Debugging Tips

### Check Logs
```
GET /user/tickets
  ↓
TicketController.listTickets()
  ↓
TicketService.getTicketsByRequesterId()
  ↓
TicketDAO.findByRequesterId()
  ↓
Hibernate: SELECT ... FROM tickets WHERE requesterId = ?
```

### Common Issues
| Problem | Solution |
|---------|----------|
| Categories not showing | Check if DB has records in ticket_categories |
| Tickets not saving | Check if ticket_statuses has "OPEN" status |
| Access denied | Verify you own the ticket (requesterId) |
| Form errors | Check browser console for validation errors |

---

## 📊 Status Values

Common status values:
- `OPEN` - New ticket
- `IN_PROGRESS` - Being worked on
- `CLOSED` - Resolved

(Add more as needed in database)

---

## 🎨 CSS Classes

### Status Badges
```css
.status-open { background: yellow, text: dark }
.status-in-progress { background: blue, text: dark }
.status-closed { background: green, text: dark }
```

### Form Elements
```css
input, select, textarea { border: 1px solid var(--line) }
input:focus { border-color: var(--accent), box-shadow: glow }
```

---

## 📱 Mobile Support

All pages are responsive:
- Tables become stackable
- Dropdowns work on touch
- Buttons are large enough
- Padding appropriate for small screens

---

## 🔄 Data Flow

### Create Operation
```
Form Submit
  ↓
UserController.createTicket()
  ↓
Validate inputs
  ↓
TicketService.createTicket()
  ↓
Create Ticket object
  ↓
TicketDAO.save()
  ↓
Hibernate persist
  ↓
Insert into tickets table
  ↓
Redirect to /user/tickets
```

### Read Operation
```
Click "View Tickets"
  ↓
GET /user/tickets
  ↓
UserController.listTickets()
  ↓
TicketService.getTicketsByRequesterId(userId)
  ↓
TicketDAO.findByRequesterId()
  ↓
Hibernate execute query
  ↓
Model.addAttribute("tickets", list)
  ↓
Render liste.jsp
  ↓
JSTL forEach loop
  ↓
Display table
```

---

## 📚 Related Files

### Documentation
- TICKET_SYSTEM_GUIDE.md - Complete guide
- TICKET_IMPLEMENTATION_SUMMARY.md - Implementation details

### Source Code
- TicketDAO.java - Data access
- TicketService.java - Business logic
- UserController.java - Request handling
- tickets/*.jsp - Views

---

## 🚀 Deployment Checklist

- [ ] Database tables created
- [ ] ticket_statuses has "OPEN" status
- [ ] ticket_categories has at least one category
- [ ] DAOs autowired in Service
- [ ] Service autowired in Controller
- [ ] JSP files in correct location
- [ ] Context path correct in JSP href
- [ ] Test in browser
- [ ] Check console for errors
- [ ] Verify database saves

---

## 💡 Quick Tips

### Add New Status
```sql
INSERT INTO ticket_statuses (StatusName) VALUES ('PENDING');
```

### Add New Category
```sql
INSERT INTO ticket_categories (CategoryName) VALUES ('Feedback');
```

### Query User's Tickets
```sql
SELECT * FROM tickets WHERE RequesterId = ? ORDER BY CreatedAt DESC;
```

### Check Ticket Details
```sql
SELECT t.*, u.FullName, c.CategoryName, s.StatusName 
FROM tickets t
JOIN users u ON t.RequesterId = u.user_id
JOIN ticket_categories c ON t.CategoryId = c.CategoryId
JOIN ticket_statuses s ON t.StatusId = s.StatusId
WHERE t.TicketId = ?;
```

---

## ✨ What's Working

- ✅ Create new tickets
- ✅ List user's tickets
- ✅ View ticket details
- ✅ Category selection
- ✅ Session-based authentication
- ✅ Ownership validation
- ✅ Error handling
- ✅ Responsive UI
- ✅ Modern styling

---

## 🎯 Immediate Next Steps

1. **Test** - Follow testing checklist
2. **Deploy** - Push to Tomcat
3. **Verify** - Check all endpoints work
4. **Monitor** - Watch logs for errors
5. **Enhance** - Add features as needed

---

**For detailed info, see TICKET_SYSTEM_GUIDE.md**

**For implementation details, see TICKET_IMPLEMENTATION_SUMMARY.md**

