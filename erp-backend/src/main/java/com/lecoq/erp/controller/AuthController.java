package com.lecoq.erp.controller;

import com.lecoq.erp.config.JwtTokenUtil;
import com.lecoq.erp.dto.ApiResponse;
import com.lecoq.erp.dto.JwtResponse;
import com.lecoq.erp.dto.LoginRequest;
import com.lecoq.erp.entity.Usuario;
import com.lecoq.erp.service.CustomUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            Usuario userPrincipal = (Usuario) authentication.getPrincipal();
            String jwt = jwtTokenUtil.generateToken(userPrincipal);

            JwtResponse jwtResponse = new JwtResponse(
                    jwt,
                    userPrincipal.getId(),
                    userPrincipal.getUsername(),
                    userPrincipal.getNombreCompleto(),
                    userPrincipal.getEmail(),
                    userPrincipal.getRol()
            );

            log.info("Usuario autenticado exitosamente: {}", userPrincipal.getUsername());
            
            return ResponseEntity.ok(ApiResponse.success("Login exitoso", jwtResponse));

        } catch (BadCredentialsException e) {
            log.error("Credenciales inválidas para usuario: {}", loginRequest.getUsername());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Credenciales inválidas"));
        } catch (Exception e) {
            log.error("Error durante la autenticación: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error durante la autenticación: " + e.getMessage()));
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                String username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                Usuario userDetails = (Usuario) userDetailsService.loadUserByUsername(username);
                
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    return ResponseEntity.ok(ApiResponse.success("Token válido", userDetails));
                }
            }
            return ResponseEntity.badRequest().body(ApiResponse.error("Token inválido"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Error validando token: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logout exitoso"));
    }
}
