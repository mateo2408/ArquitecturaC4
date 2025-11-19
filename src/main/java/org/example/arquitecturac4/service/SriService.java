package org.example.arquitecturac4.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.arquitecturac4.dto.ContribuyenteResponse;
import org.example.arquitecturac4.dto.DatosContribuyente;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class SriService {

    private static final String SRI_BASE_URL = "https://srienlinea.sri.gob.ec/sri-catastro-sujeto-servicio-internet/rest/ConsolidadoContribuyente";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public SriService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Cacheable(value = "contribuyentes", key = "#ruc")
    public ContribuyenteResponse verificarContribuyente(String ruc) {
        log.info("Verificando contribuyente con RUC: {}", ruc);

        try {
            // 1. Verificar si existe el contribuyente
            String urlExiste = SRI_BASE_URL + "/existePorNumeroRuc?numeroRuc=" + ruc;
            String responseExiste = restTemplate.getForObject(urlExiste, String.class);

            log.debug("Respuesta existePorNumeroRuc: {}", responseExiste);

            JsonNode rootNode = objectMapper.readTree(responseExiste);

            if (rootNode == null || !rootNode.has("existeContribuyente")) {
                return new ContribuyenteResponse(false, false,
                    "Error al verificar el contribuyente en el SRI", null);
            }

            boolean existe = rootNode.get("existeContribuyente").asBoolean();

            if (!existe) {
                return new ContribuyenteResponse(false, false,
                    "El RUC no está registrado como contribuyente en el SRI", null);
            }

            // 2. Obtener datos del contribuyente
            String urlDatos = SRI_BASE_URL + "/obtenerPorNumerosRuc?ruc=" + ruc;
            String responseDatos = restTemplate.getForObject(urlDatos, String.class);

            log.debug("Respuesta obtenerPorNumerosRuc: {}", responseDatos);

            JsonNode datosNode = objectMapper.readTree(responseDatos);

            if (datosNode == null || !datosNode.isArray() || datosNode.size() == 0) {
                return new ContribuyenteResponse(true, false,
                    "No se pudieron obtener los datos del contribuyente", null);
            }

            JsonNode contribuyente = datosNode.get(0);

            // 3. Verificar si es persona natural
            String tipoContribuyente = contribuyente.has("tipoContribuyente")
                ? contribuyente.get("tipoContribuyente").asText() : "";

            boolean esPersonaNatural = tipoContribuyente.toUpperCase().contains("NATURAL")
                || ruc.length() == 13 && ruc.substring(10, 13).equals("001");

            // 4. Mapear datos del contribuyente
            DatosContribuyente datos = new DatosContribuyente();
            datos.setNumeroRuc(getValue(contribuyente, "numeroRuc"));
            datos.setRazonSocial(getValue(contribuyente, "razonSocial"));
            datos.setNombreComercial(getValue(contribuyente, "nombreComercial"));
            datos.setTipoContribuyente(tipoContribuyente);
            datos.setEstadoContribuyente(getValue(contribuyente, "estadoContribuyente"));
            datos.setNombreFantasiaComercial(getValue(contribuyente, "nombreFantasiaComercial"));
            datos.setDireccionCompleta(getValue(contribuyente, "direccionCompleta"));
            datos.setTipoIdentificacion(getValue(contribuyente, "tipoIdentificacion"));

            String mensaje = esPersonaNatural
                ? "Contribuyente verificado correctamente"
                : "El RUC no corresponde a una persona natural";

            return new ContribuyenteResponse(true, esPersonaNatural, mensaje, datos);

        } catch (Exception e) {
            log.error("Error al consultar el SRI: {}", e.getMessage(), e);
            return new ContribuyenteResponse(false, false,
                "Error al conectar con el SRI: " + e.getMessage(), null);
        }
    }

    private String getValue(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
            ? node.get(field).asText()
            : "";
    }
}

