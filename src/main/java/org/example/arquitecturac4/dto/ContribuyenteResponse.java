package org.example.arquitecturac4.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContribuyenteResponse {
    private boolean esContribuyente;
    private boolean esPersonaNatural;
    private String mensaje;
    private DatosContribuyente datosContribuyente;
}
