package org.example.arquitecturac4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponse {
    private boolean encontrado;
    private String mensaje;
    private DatosVehiculo datosVehiculo;
}

