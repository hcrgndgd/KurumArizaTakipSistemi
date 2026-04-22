package com.JavaProje.KurumArizaTakipSistemi.web;

import com.JavaProje.KurumArizaTakipSistemi.model.Role;
import com.JavaProje.KurumArizaTakipSistemi.model.User;
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
}
