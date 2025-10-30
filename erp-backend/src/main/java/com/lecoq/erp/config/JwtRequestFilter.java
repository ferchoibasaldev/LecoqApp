package com.lecoq.erp.config;

import com.lecoq.erp.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String path   = request.getRequestURI();
        final String method = request.getMethod();
        final String ip     = getClientIp(request);

        log.debug("[JWT] >>> {} {} from {}", method, path, ip);

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            log.debug("[JWT] No Authorization header -> no intento de autenticación JWT");
            chain.doFilter(request, response);
            return;
        }

        if (!authHeader.startsWith("Bearer ")) {
            log.warn("[JWT] Authorization sin 'Bearer ' -> value='{}'", authHeader);
            chain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            log.warn("[JWT] Token vacío después de 'Bearer '");
            chain.doFilter(request, response);
            return;
        }

        String username = null;
        try {
            username = jwtTokenUtil.getUsernameFromToken(token);
            log.debug("[JWT] Username extraído del token: {}", username);
        } catch (Exception e) {
            // Si usas jjwt, aquí podrías distinguir por tipos (ExpiredJwtException, SignatureException, etc.)
            log.warn("[JWT] No se pudo extraer username del token. Causa={} Msg={}",
                    e.getClass().getSimpleName(), e.getMessage());
            // No seteamos autenticación; dejar que el EntryPoint maneje el 401 si la ruta lo requiere
            chain.doFilter(request, response);
            return;
        }

        if (username == null) {
            log.warn("[JWT] Username es null tras parsear el token");
            chain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            log.debug("[JWT] Ya existe autenticación en el contexto -> {}",
                    SecurityContextHolder.getContext().getAuthentication().getName());
            chain.doFilter(request, response);
            return;
        }

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean valid = jwtTokenUtil.validateToken(token, userDetails);

            if (!valid) {
                log.warn("[JWT] Token inválido para usuario {}", username);
                chain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.info("[JWT] Autenticación establecida para usuario {} en {} {}", username, method, path);

        } catch (Exception e) {
            log.error("[JWT] Error durante validación/autenticación. Causa={} Msg={}",
                    e.getClass().getSimpleName(), e.getMessage());
            // No setear auth: dejar que la cadena y el EntryPoint actúen
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            // toma el primero (cliente real)
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
