package org.example.arquitecturac4.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatosContribuyente {
    private String numeroRuc;
    private String razonSocial;
    private String nombreComercial;
    private String tipoContribuyente;
    private String estadoContribuyente;
    private String nombreFantasiaComercial;
    private String direccionCompleta;
    private String tipoIdentificacion;
}
