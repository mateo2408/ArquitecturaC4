package org.example.arquitecturac4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenciaResponse {
    private boolean exitoso;
    private String mensaje;
    private Integer puntos;
    private String cedula;
    private String placa;
    private boolean desdeCahe;
}

