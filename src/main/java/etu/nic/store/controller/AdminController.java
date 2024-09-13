package etu.nic.store.controller;

import etu.nic.store.model.dto.UserDto;
import etu.nic.store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    UserService userService;

    @PostMapping("/give-admin/{userId}")
    public ResponseEntity<UserDto> giveAdminRole(@PathVariable Long userId) {
        UserDto updatedUser = userService.giveAdminRole(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedUser);
    }
    @PutMapping("/update-user/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.updateUser(userId, userDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(updatedUser);
    }
    @DeleteMapping("/archive/{userId}")
    public ResponseEntity<Void> archiveUser(@PathVariable Long userId) {
        userService.archiveUser(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

    @PutMapping("/restore/{userId}")
    public ResponseEntity<Void> restoreUser(@PathVariable Long userId) {
        userService.restoreUser(userId);

        return ResponseEntity.status(HttpStatus.OK)
                .build();
    }

}
