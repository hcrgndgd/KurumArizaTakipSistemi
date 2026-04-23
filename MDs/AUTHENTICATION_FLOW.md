# Authentication & Post-Login Profile Flow Documentation

## Overview
This document explains the complete authentication flow and how users are redirected to their profile after login.

---

## 1. Controller Mappings

### AuthController (`/auth`)
- **POST /auth/register** - User registration with email verification
- **POST /auth/login** - User login (stores user in session)
- **POST /auth/logout** - User logout (invalidates session)
- **GET /auth/verify** - Email verification callback
- **POST /auth/resend-verification** - Resend verification email

### UserController (`/user`)
- **GET /user/profile** - Display user profile page (requires authentication)
- **GET /user/tickets** - View user's tickets (future implementation)
- **GET /user/tickets/new** - Create new ticket page (future implementation)

---

## 2. Authentication Flow: Step-by-Step

### Login Process

```
1. User visits /login page
2. User enters email & password in login.jsp form
3. JavaScript sends POST request to /auth/login with credentials
4. AuthController.login() method:
   - Validates email & password via UserService.login()
   - If valid:
     * Stores User object in session as "currentUser"
     * Stores user's role name in session as "userRole"
     * Returns JSON response with success message
   - If invalid: Returns error response

5. login.jsp JavaScript receives response:
   - Shows success message
   - Waits 1.5 seconds
   - Redirects to /user/profile

6. UserController.profile() method:
   - Checks if "currentUser" exists in session
   - If null → Redirects to /login (not authenticated)
   - If exists → Passes user data to Model
   - Returns "user-profile" JSP view

7. user-profile.jsp displays:
   - User's full name
   - User's email
   - User's role
   - "Create Ticket" button
   - "View Tickets" button
   - "Logout" button
```

---

## 3. Session Management

### How Sessions Work
- **Session Creation**: Automatic by servlet container when request comes in
- **Session Attributes Set During Login**:
  - `currentUser` → User object (contains full name, email, role, etc.)
  - `userRole` → String (role name like "USER" or "ADMIN")

### Accessing Session Data in JSP
```jsp
<%@ page import="com.JavaProje.KurumArizaTakipSistemi.model.User" %>

<!-- Get the current user from session -->
<%
    User currentUser = (User) session.getAttribute("currentUser");
    String userRole = (String) session.getAttribute("userRole");
%>

<!-- Or use EL (Expression Language) -->
${user.fullName}
${userRole}
```

### Session Lifecycle
1. **Created**: First request after login
2. **Maintained**: Sent with every subsequent request via cookies
3. **Invalidated**: When user clicks logout (session.invalidate() called in AuthController)

---

## 4. Required Endpoints Summary

| Method | URL | Description | Auth Required | Redirects To |
|--------|-----|-------------|---|---|
| GET | `/login` | Login page (public) | No | - |
| POST | `/auth/login` | Login API endpoint | No | - |
| GET | `/user/profile` | User profile page | Yes | `/login` if not auth |
| POST | `/auth/logout` | Logout API endpoint | Yes | - |
| GET | `/user/tickets` | View tickets page | Yes | (TBD) |
| GET | `/user/tickets/new` | Create ticket page | Yes | (TBD) |

---

## 5. Profile Page Display Elements

### User Information Section
- **Full Name**: From `${user.fullName}`
- **Email**: From `${user.email}`
- **Role**: From `${userRole}` or `${user.role.roleName}`

### Action Buttons

#### Create Ticket Button
- **Text**: "📝 Create Ticket"
- **Link**: `<%= request.getContextPath() %>/user/tickets/new`
- **Description**: "Report a new issue or submit a complaint to the system."

#### View Tickets Button
- **Text**: "📋 View Tickets"
- **Link**: `<%= request.getContextPath() %>/user/tickets`
- **Description**: "See all your submitted tickets and track their status."

#### Logout Button
- **Text**: "Logout"
- **Type**: JavaScript function `logout()`
- **Behavior**: 
  - Confirms with user
  - Sends POST to `/auth/logout`
  - Invalidates session
  - Redirects to `/login`

---

## 6. View Names (JSP Files)

| View Name | File Path | Purpose |
|-----------|-----------|---------|
| `user-profile` | `/WEB-INF/view/user-profile.jsp` | User profile page with stats & actions |
| `login` | `/WEB-INF/view/login.jsp` | Login form page |
| `register` | `/WEB-INF/view/register.jsp` | Registration form page |

---

## 7. Logout Flow

```
1. User clicks "Logout" button on profile page
2. Browser shows confirmation dialog
3. user-profile.jsp JavaScript sends POST to /auth/logout
4. AuthController.logout() method:
   - Invalidates session
   - Returns success response
5. JavaScript receives response
6. Redirects to /login after 1 second
```

---

## 8. Error Handling

### Session Expires
- If user's session expires naturally:
  - Attempt to access `/user/profile` will have `currentUser = null`
  - Redirects to `/login`
  - User must log in again

### Invalid Credentials
- If wrong email/password provided:
  - `/auth/login` returns HTTP 401 UNAUTHORIZED
  - Error message displayed on login page
  - No session created

### Not Authenticated
- If user tries to access `/user/profile` without logging in:
  - `UserController.profile()` checks for `currentUser`
  - Redirects to `/login`

---

## 9. Code References

### AuthController - Login Method
```java
@PostMapping("/login")
public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
    // ...
    session.setAttribute("currentUser", user);      // Store user object
    session.setAttribute("userRole", roleName);     // Store role
    // ...
}
```

### UserController - Profile Method
```java
@GetMapping("/profile")
public String profile(HttpSession session, Model model) {
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) {
        return "redirect:/login";  // Not authenticated
    }
    model.addAttribute("user", currentUser);
    return "user-profile";  // Show profile page
}
```

### login.jsp - JavaScript Redirect
```javascript
setTimeout(() => {
    window.location.href = "<%= request.getContextPath() %>/user/profile";
}, 1500);
```

---

## 10. Testing the Flow

### Steps to Test
1. Start the application
2. Navigate to `/login`
3. Enter valid credentials (verified user)
4. See success message
5. Get redirected to `/user/profile`
6. Verify user name, email, and role are displayed
7. Click on "Create Ticket" or "View Tickets" buttons
8. Click "Logout" and confirm logout
9. Verify redirect back to `/login`

### Manual Testing Checklist
- [ ] Login with valid credentials → redirects to profile
- [ ] Profile shows correct user name
- [ ] Profile shows correct user email
- [ ] Profile shows correct user role
- [ ] "Create Ticket" button links to `/user/tickets/new`
- [ ] "View Tickets" button links to `/user/tickets`
- [ ] "Logout" button works and invalidates session
- [ ] Accessing `/user/profile` without login → redirects to `/login`
- [ ] Session persists across page refreshes
- [ ] Session invalidated after logout
