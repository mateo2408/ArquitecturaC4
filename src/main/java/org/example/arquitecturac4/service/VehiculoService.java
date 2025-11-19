package org.example.arquitecturac4.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.arquitecturac4.dto.DatosVehiculo;
import org.example.arquitecturac4.dto.VehiculoResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class VehiculoService {

    private static final String VEHICULO_URL = "https://srienlinea.sri.gob.ec/sri-matriculacion-vehicular-recaudacion-servicio-internet/rest/BaseVehiculo/obtenerPorNumeroPlacaOPorNumeroCampvOPorNumeroCpn";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public VehiculoService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Cacheable(value = "vehiculos", key = "#placa")
    public VehiculoResponse consultarVehiculo(String placa) {
        log.info("Consultando vehículo con placa: {}", placa);

        try {
            String url = VEHICULO_URL + "?numeroPlacaCampvCpn=" + placa.toUpperCase();
            String response = restTemplate.getForObject(url, String.class);

            log.debug("Respuesta consulta vehículo: {}", response);

            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode == null || rootNode.isNull()) {
                return new VehiculoResponse(false,
                    "No se encontró información del vehículo con placa: " + placa, null);
            }

            // El SRI puede devolver un array o un objeto
            JsonNode vehiculoNode = rootNode;
            if (rootNode.isArray() && rootNode.size() > 0) {
                vehiculoNode = rootNode.get(0);
            }

            if (vehiculoNode.isNull() || vehiculoNode.isEmpty()) {
                return new VehiculoResponse(false,
                    "No se encontró información del vehículo con placa: " + placa, null);
            }

            // Mapear datos del vehículo
            DatosVehiculo datos = new DatosVehiculo();
            datos.setPlaca(getValue(vehiculoNode, "placa"));
            datos.setMarca(getValue(vehiculoNode, "marca"));
            datos.setModelo(getValue(vehiculoNode, "modelo"));
            datos.setAnio(getValue(vehiculoNode, "anio"));
            datos.setClase(getValue(vehiculoNode, "clase"));
            datos.setColor(getValue(vehiculoNode, "color"));
            datos.setNumeroMotor(getValue(vehiculoNode, "numeroMotor"));
            datos.setNumeroChasis(getValue(vehiculoNode, "numeroChasis"));
            datos.setCilindraje(getValue(vehiculoNode, "cilindraje"));
            datos.setPropietario(getValue(vehiculoNode, "propietario"));

            return new VehiculoResponse(true,
                "Vehículo encontrado correctamente", datos);

        } catch (Exception e) {
            log.error("Error al consultar vehículo: {}", e.getMessage(), e);
            return new VehiculoResponse(false,
                "Error al consultar el vehículo: " + e.getMessage(), null);
        }
    }

    private String getValue(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
            ? node.get(field).asText()
            : "";
    }
}

