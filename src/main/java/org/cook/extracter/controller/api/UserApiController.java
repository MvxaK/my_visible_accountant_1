package org.cook.extracter.controller.api;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.Role;
import org.cook.extracter.model.User;
import org.cook.extracter.model.UserCreateRequest;
import org.cook.extracter.security.auth.RegisterRequest;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);

        return ResponseEntity.ok(user);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userService.getUserById(userDetails.getId());

        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody UserCreateRequest request){
        User user = userService.createUser(request);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/update-info")
    public ResponseEntity<Void> updateUserInfo(@PathVariable Long id, @RequestParam String newEmail, @RequestParam String currentPassword, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        userService.updateUser(id, newEmail, currentPassword, userDetails.getId(), isAdmin);

        return ResponseEntity.noContent()
                .build();
    }

    @PatchMapping("/{id}/update-password")
    public ResponseEntity<Void> updateUserPassword(@PathVariable Long id, @RequestParam String currentPassword, @RequestParam String newPassword, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        userService.updatePassword(id, currentPassword, newPassword, userDetails.getId(), isAdmin);

        return ResponseEntity.noContent()
                .build();
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        userService.deleteUser(id, userDetails.getId(), isAdmin);

        return ResponseEntity.noContent()
                .build();
    }
}