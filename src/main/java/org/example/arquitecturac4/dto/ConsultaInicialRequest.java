package org.example.arquitecturac4.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaInicialRequest {
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico debe ser válido")
    private String correo;
    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "\\d{13}", message = "El RUC debe tener 13 dígitos")
    private String ruc;
}
