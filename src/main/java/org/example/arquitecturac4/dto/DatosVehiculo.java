package org.example.arquitecturac4.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosVehiculo {
    private String placa;
    private String marca;
    private String modelo;
    private String anio;
    private String clase;
    private String color;
    private String numeroMotor;
    private String numeroChasis;
    private String cilindraje;
    private String propietario;
}

