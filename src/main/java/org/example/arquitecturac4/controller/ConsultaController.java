package org.example.arquitecturac4.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arquitecturac4.dto.*;
import org.example.arquitecturac4.service.AntService;
import org.example.arquitecturac4.service.SriService;
import org.example.arquitecturac4.service.VehiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ConsultaController {

    private final SriService sriService;
    private final VehiculoService vehiculoService;
    private final AntService antService;

    /**
     * Endpoint 1: Verificar si el RUC es contribuyente y persona natural
     */
    @PostMapping("/verificar-contribuyente")
    public ResponseEntity<?> verificarContribuyente(
            @Valid @RequestBody ConsultaInicialRequest request,
            BindingResult bindingResult) {

        log.info("Solicitud de verificación de contribuyente - RUC: {}, Correo: {}",
                request.getRuc(), request.getCorreo());

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            ContribuyenteResponse response = sriService.verificarContribuyente(request.getRuc());

            if (!response.isEsContribuyente()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            if (!response.isEsPersonaNatural()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al verificar contribuyente: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno al verificar el contribuyente"));
        }
    }

    /**
     * Endpoint 2: Consultar información del vehículo
     */
    @PostMapping("/consultar-vehiculo")
    public ResponseEntity<?> consultarVehiculo(
            @Valid @RequestBody VehiculoRequest request,
            BindingResult bindingResult) {

        log.info("Solicitud de consulta de vehículo - Placa: {}", request.getPlaca());

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            VehiculoResponse response = vehiculoService.consultarVehiculo(request.getPlaca());

            if (!response.isEncontrado()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al consultar vehículo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno al consultar el vehículo"));
        }
    }

    /**
     * Endpoint 3: Consultar puntos de licencia en la ANT
     */
    @PostMapping("/consultar-licencia")
    public ResponseEntity<?> consultarLicencia(
            @Valid @RequestBody LicenciaRequest request,
            BindingResult bindingResult) {

        log.info("Solicitud de consulta de licencia - Cédula: {}, Placa: {}",
                request.getCedula(), request.getPlaca());

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage()
                    ));
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        try {
            LicenciaResponse response = antService.consultarPuntosLicencia(
                    request.getCedula(),
                    request.getPlaca()
            );

            if (!response.isExitoso()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al consultar licencia: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error interno al consultar la licencia"));
        }
    }

    /**
     * Endpoint de salud
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Sistema de Consultas SRI y ANT");
        response.put("version", "1.0.0");
        return ResponseEntity.ok(response);
    }
}

