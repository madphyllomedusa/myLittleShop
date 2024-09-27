package etu.nic.store.controller;

import etu.nic.store.model.dto.JwtAuthenticationResponse;
import etu.nic.store.model.dto.SignInRequest;
import etu.nic.store.model.dto.UserDto;
import etu.nic.store.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")  // разрешаем запросы с фронтенда
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserAuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody UserDto userDto) {
        UserDto registeredUser = userService.saveUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticate(@RequestBody SignInRequest signInRequest) {
        JwtAuthenticationResponse jwtResponse = userService.loginUser(signInRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}
