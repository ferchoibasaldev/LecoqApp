package com.lecoq.erp.dto;

import com.lecoq.erp.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String nombreCompleto;
    private String email;
    private Usuario.Rol rol;
    
    public JwtResponse(String accessToken, Long id, String username, String nombreCompleto, String email, Usuario.Rol rol) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.rol = rol;
    }
}
