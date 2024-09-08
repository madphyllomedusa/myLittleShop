package etu.nic.store.controller;

import etu.nic.store.model.dto.UserDto;
import etu.nic.store.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto,  HttpServletResponse response) {
        UserDto registeredUser = userService.saveUser(userDto,response);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserDto userDto,  HttpServletResponse response) {

        UserDto authenticatedUser = userService.loginUser(userDto,response);
        return ResponseEntity.ok(authenticatedUser);

    }
}
