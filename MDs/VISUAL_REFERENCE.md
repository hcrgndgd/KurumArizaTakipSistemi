# Visual Reference: Profile Flow Architecture

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         WEB BROWSER (Client)                         │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │ login.jsp                                                     │  │
│  │ - Email input                                                 │  │
│  │ - Password input                                              │  │
│  │ - Login button                                                │  │
│  │ - JavaScript to POST /auth/login                              │  │
│  │ - Redirect to /user/profile after success                    │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                              ↕ (HTTP)                                │
└─────────────────────────────────────────────────────────────────────┘
                                 ↕↕
        ┌────────────────────────────────────────────┐
        │   HTTP Server (Tomcat/Spring Container)    │
        │                                            │
        │  ┌──────────────────────────────────────┐  │
        │  │      @RestController                │  │
        │  │      AuthController                 │  │
        │  │                                      │  │
        │  │  POST /auth/login {email, pwd}      │  │
        │  │  ├─ Validates with UserService      │  │
        │  │  ├─ Creates session                 │  │
        │  │  │  ├─ currentUser = User object    │  │
        │  │  │  └─ userRole = "USER" / "ADMIN" │  │
        │  │  └─ Returns JSON response           │  │
        │  │                                      │  │
        │  │  POST /auth/logout                  │  │
        │  │  ├─ Invalidates session             │  │
        │  │  └─ Returns JSON response           │  │
        │  └──────────────────────────────────────┘  │
        │                                            │
        │  ┌──────────────────────────────────────┐  │
        │  │      @Controller                    │  │
        │  │      UserController                 │  │
        │  │                                      │  │
        │  │  GET /user/profile                  │  │
        │  │  ├─ Check session.currentUser       │  │
        │  │  ├─ If null → redirect:/login       │  │
        │  │  ├─ Else → add user to Model        │  │
        │  │  └─ Return "user-profile" (JSP)     │  │
        │  └──────────────────────────────────────┘  │
        │                                            │
        │  ┌──────────────────────────────────────┐  │
        │  │      @Service                       │  │
        │  │      UserService                    │  │
        │  │                                      │  │
        │  │  login(email, password)             │  │
        │  │  ├─ Find user in DB                 │  │
        │  │  ├─ Check verified status           │  │
        │  │  ├─ Validate password hash          │  │
        │  │  └─ Return User object or throw     │  │
        │  └──────────────────────────────────────┘  │
        │                                            │
        │  ┌──────────────────────────────────────┐  │
        │  │      Database (Hibernate)           │  │
        │  │                                      │  │
        │  │  users table                        │  │
        │  │  ├─ user_id (PK)                    │  │
        │  │  ├─ full_name                       │  │
        │  │  ├─ email (UNIQUE)                  │  │
        │  │  ├─ password_hash (MD5)             │  │
        │  │  ├─ is_verified (BOOLEAN)           │  │
        │  │  ├─ role_id (FK)                    │  │
        │  │  └─ verification_token              │  │
        │  │                                      │  │
        │  │  roles table                        │  │
        │  │  ├─ role_id (PK)                    │  │
        │  │  └─ role_name ("USER", "ADMIN")     │  │
        │  └──────────────────────────────────────┘  │
        │                                            │
        └────────────────────────────────────────────┘
```

---

## Request/Response Flow Diagram

### Login Process
```
┌──────────────┐
│ User Browser │
└──────┬───────┘
       │ 1. Click login.jsp form submit
       │ POST /auth/login
       │ Headers: Content-Type: application/json
       ├─ Body: {"email": "user@example.com", "password": "pass123"}
       │
       ▼
┌──────────────────────────────────────────┐
│ AuthController.login()                   │
│ @PostMapping("/auth/login")              │
│                                          │
│ 1. Extract email & password from body    │
│ 2. Call userService.login()              │
│    - Validates credentials               │
│    - Returns User object or throws       │
│ 3. Create session attributes:            │
│    - session.setAttribute(               │
│        "currentUser", user)              │
│    - session.setAttribute(               │
│        "userRole", roleName)             │
│ 4. Return ResponseEntity.ok()            │
└──────┬───────────────────────────────────┘
       │ 2. HTTP Response (200 OK)
       │ Headers: Set-Cookie: JSESSIONID=...
       │ Body: {
       │   "message": "Login successful",
       │   "role": "USER",
       │   "fullName": "John Doe"
       │ }
       │
       ▼
┌──────────────────────────────────────────┐
│ login.jsp JavaScript                     │
│                                          │
│ 1. Parse JSON response                   │
│ 2. Show success message                  │
│ 3. Wait 1.5 seconds                      │
│ 4. window.location.href =                │
│    "/KurumArizaTakipSistemi/user/profile"│
└──────┬───────────────────────────────────┘
       │ 3. GET /user/profile
       │ Headers: Cookie: JSESSIONID=...
       │
       ▼
┌──────────────────────────────────────────┐
│ UserController.profile()                 │
│ @GetMapping("/profile")                  │
│                                          │
│ 1. session.getAttribute("currentUser")   │
│ 2. If null → return "redirect:/login"    │
│ 3. Else:                                 │
│    - model.addAttribute("user", user)    │
│    - model.addAttribute("role", role)    │
│    - return "user-profile"               │
└──────┬───────────────────────────────────┘
       │ 4. HTTP Response (200 OK)
       │ Body: HTML (user-profile.jsp)
       │  - ${user.fullName}
       │  - ${user.email}
       │  - ${userRole}
       │  - Buttons for tickets & logout
       │
       ▼
┌──────────────────────────────────────────┐
│ Browser Display                          │
│                                          │
│ ┌──────────────────────────────────────┐ │
│ │ My Profile                  [Logout] │ │
│ ├──────────────────────────────────────┤ │
│ │ Full Name: John Doe                  │ │
│ │ Email: john@example.com              │ │
│ │ Role: USER                           │ │
│ ├──────────────────────────────────────┤ │
│ │ [Create Ticket]    [View Tickets]    │ │
│ └──────────────────────────────────────┘ │
└──────────────────────────────────────────┘
```

---

## Logout Process
```
┌──────────────────────────────────────────┐
│ Profile Page (user-profile.jsp)          │
│ User clicks [Logout] button              │
└──────┬───────────────────────────────────┘
       │ 1. Show confirmation dialog
       │ User clicks OK
       │ POST /auth/logout
       │ Headers: Cookie: JSESSIONID=...
       │
       ▼
┌──────────────────────────────────────────┐
│ AuthController.logout()                  │
│ @PostMapping("/auth/logout")             │
│                                          │
│ 1. Get email from currentUser (log)      │
│ 2. session.invalidate()                  │
│    - Removes all attributes              │
│    - Removes JSESSIONID cookie           │
│ 3. Return ResponseEntity.ok()            │
└──────┬───────────────────────────────────┘
       │ 2. HTTP Response (200 OK)
       │ Headers: Set-Cookie: JSESSIONID=;
       │          Max-Age=0
       │ Body: {"message": "Logout successful"}
       │
       ▼
┌──────────────────────────────────────────┐
│ Profile Page JavaScript                  │
│                                          │
│ 1. Parse response                        │
│ 2. Show success message                  │
│ 3. Wait 1 second                         │
│ 4. window.location.href = "/login"       │
└──────┬───────────────────────────────────┘
       │ 3. GET /login
       │ (Session cookie expired)
       │
       ▼
┌──────────────────────────────────────────┐
│ Login Page Displayed                     │
│ Ready for next user to login             │
└──────────────────────────────────────────┘
```

---

## Session Data Flow

### Session Storage
```
┌─────────────────────────────────────────────────┐
│           HTTP Session (Server Memory)           │
│           JSESSIONID: abc123def456              │
│ ┌───────────────────────────────────────────┐   │
│ │ Attributes:                               │   │
│ │                                           │   │
│ │ "currentUser" →                           │   │
│ │   User {                                  │   │
│ │     userId: 1                             │   │
│ │     fullName: "John Doe"                  │   │
│ │     email: "john@example.com"             │   │
│ │     role: Role {                          │   │
│ │       roleId: 1                           │   │
│ │       roleName: "USER"                    │   │
│ │     }                                     │   │
│ │   }                                       │   │
│ │                                           │   │
│ │ "userRole" → "USER"                       │   │
│ └───────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
         ↑                                    
         │ Browser sends cookie with each request
         │ Set-Cookie: JSESSIONID=abc123def456
         │
         └───→ [Browser Session Cookie Storage]
               JSESSIONID=abc123def456
```

---

## Profile Page Layout

```
┌─────────────────────────────────────────────────────────┐
│ My Profile                            [Logout]          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  Full Name                                              │
│  ┌─────────────────────────────────────────────────┐   │
│  │ John Doe                                        │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  Email                                                  │
│  ┌─────────────────────────────────────────────────┐   │
│  │ john@ogr.duzce.edu.tr                          │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
│  Role                                                   │
│  ┌─────────────────────────────────────────────────┐   │
│  │ USER                                            │   │
│  └─────────────────────────────────────────────────┘   │
│                                                         │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌────────────────────┐  ┌────────────────────┐        │
│  │ 📝 Create Ticket   │  │ 📋 View Tickets    │        │
│  │                    │  │                    │        │
│  │ Report a new issue │  │ See all your       │        │
│  │ or submit a        │  │ submitted tickets  │        │
│  │ complaint.         │  │ and track status.  │        │
│  │                    │  │                    │        │
│  │ [Create Ticket]    │  │ [View Tickets]     │        │
│  └────────────────────┘  └────────────────────┘        │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

---

## File Organization

```
KurumArizaTakipSistemi/
├── src/main/
│   ├── java/com/JavaProje/KurumArizaTakipSistemi/
│   │   ├── web/
│   │   │   ├── AuthController.java          ← Login/Logout
│   │   │   ├── UserController.java          ← Profile page
│   │   │   ├── AdminController.java
│   │   │   └── MainController.java
│   │   ├── service/
│   │   │   ├── UserService.java             ← Authentication logic
│   │   │   └── EmailService.java
│   │   ├── model/
│   │   │   ├── User.java                    ← User entity
│   │   │   ├── Role.java                    ← Role entity
│   │   │   ├── Ticket.java
│   │   │   └── TicketCategory.java
│   │   ├── dao/
│   │   │   ├── UserDAO.java
│   │   │   └── RoleDAO.java
│   │   └── config/
│   │       ├── AppConfig.java
│   │       └── WebConfig.java
│   └── webapp/WEB-INF/
│       ├── web.xml                          ← Session config
│       └── view/
│           ├── login.jsp                    ← Modified (redirect added)
│           ├── user-profile.jsp             ← Created (NEW)
│           ├── register.jsp
│           ├── admin-dashboard.jsp
│           └── tickets/
│               ├── new.jsp
│               └── liste.jsp
└── Documentation/ 
    ├── AUTHENTICATION_FLOW.md               ← Detailed docs
    ├── QUICK_REFERENCE.md                   ← Quick lookup
    ├── TICKET_ENDPOINTS_GUIDE.md            ← Future endpoints
    └── IMPLEMENTATION_SUMMARY.md            ← Overview

```

---

## Technology Stack

```
┌────────────────────────────────────────────────────┐
│             Java Web Application Stack              │
├────────────────────────────────────────────────────┤
│                                                    │
│  Presentation Layer (Frontend)                    │
│  ├─ JSP (Java Server Pages)                       │
│  ├─ JavaScript (for interactivity)                │
│  └─ CSS (styling)                                 │
│                                                    │
│  Controller Layer                                 │
│  ├─ Spring MVC Controllers                        │
│  ├─ @RestController (JSON API)                    │
│  └─ @Controller (MVC Views)                       │
│                                                    │
│  Service Layer                                    │
│  ├─ Business Logic (UserService)                  │
│  ├─ Authentication                                │
│  └─ Email Verification                            │
│                                                    │
│  Data Access Layer (DAO)                          │
│  ├─ Hibernate ORM                                 │
│  ├─ UserDAO                                       │
│  └─ RoleDAO                                       │
│                                                    │
│  Database Layer                                   │
│  ├─ MySQL / PostgreSQL                           │
│  ├─ users table                                   │
│  ├─ roles table                                   │
│  └─ tickets table                                 │
│                                                    │
│  Session Management                               │
│  ├─ Servlet Container (Tomcat)                    │
│  ├─ JSESSIONID Cookie                             │
│  └─ Server-side Session Storage                   │
│                                                    │
└────────────────────────────────────────────────────┘
```

---

## Security Flow

```
┌─────────────────────────────────────────────────────┐
│              Security Architecture                   │
├─────────────────────────────────────────────────────┤
│                                                     │
│  1. PASSWORD HASHING (Login Input)                 │
│     Raw Password → MD5 Hash → Compare with DB      │
│                                                     │
│  2. SESSION CREATION (After Login)                 │
│     User Object → Session Storage → JSESSIONID    │
│                                                    │
│  3. SESSION VALIDATION (Protected Pages)           │
│     JSESSIONID → Server → currentUser Check        │
│     If null → Redirect to /login                   │
│                                                    │
│  4. SESSION INVALIDATION (Logout)                  │
│     Logout Request → Clear Session → Delete Cookie │
│                                                    │
│  5. EMAIL VERIFICATION (Before Login)              │
│     Registration → Send Token → Verify → Activate │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## Data Models

### User Entity
```java
@Entity @Table(name = "users")
class User {
    @Id Long userId              // Primary Key
    String fullName              // Full name of user
    String email                 // Email (unique)
    String passwordHash          // MD5 hashed password
    Boolean Verified             // Email verification status
    String verificationToken     // Email verification token
    LocalDateTime tokenExpiresAt // Token expiration
    @ManyToOne Role role         // Reference to Role
}
```

### Role Entity
```java
@Entity @Table(name = "roles")
class Role {
    @Id Long roleId              // Primary Key
    String roleName              // "USER", "ADMIN", etc.
    @OneToMany Set<User> users   // Users with this role
}
```

### Session Attributes
```java
// Set during login:
session.setAttribute("currentUser", user);   // Type: User
session.setAttribute("userRole", roleName);  // Type: String

// Example values:
currentUser = User {
    userId = 1L,
    fullName = "John Doe",
    email = "john@ogr.duzce.edu.tr",
    role = Role { roleName = "USER" }
}
userRole = "USER"
```

---

This visual reference provides a complete overview of the system architecture, data flow, and implementation details for the post-login profile feature.
