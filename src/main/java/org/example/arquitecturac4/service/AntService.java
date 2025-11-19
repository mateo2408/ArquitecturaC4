package org.example.arquitecturac4.service;

import lombok.extern.slf4j.Slf4j;
import org.example.arquitecturac4.dto.LicenciaResponse;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AntService {

    private static final String ANT_URL = "https://consultaweb.ant.gob.ec/PortalWEB/paginas/clientes/clp_grid_citaciones.jsp";
    private static final int MAX_REINTENTOS = 3;
    private static final int TIEMPO_ESPERA_MS = 5000; // 5 segundos
    private static final int TIMEOUT_MS = 10000; // 10 segundos timeout

    /**
     * Consulta los puntos de licencia con caché de 24 horas
     * Se usa caché debido a la baja disponibilidad de la web de la ANT
     */
    @Cacheable(value = "licencias", key = "#cedula + '_' + #placa")
    public LicenciaResponse consultarPuntosLicencia(String cedula, String placa) {
        log.info("Consultando puntos de licencia para cédula: {} y placa: {}", cedula, placa);

        for (int intento = 1; intento <= MAX_REINTENTOS; intento++) {
            try {
                log.debug("Intento {} de {}", intento, MAX_REINTENTOS);

                // Realizar web scraping
                String url = String.format("%s?ps_tipo_identificacion=CED&ps_identificacion=%s&ps_placa=%s",
                        ANT_URL, cedula, placa.toUpperCase());

                Connection connection = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .timeout(TIMEOUT_MS)
                        .followRedirects(true)
                        .ignoreHttpErrors(false);

                Document doc = connection.get();

                // Buscar información de puntos en la página
                Integer puntos = extraerPuntos(doc);

                if (puntos != null) {
                    log.info("Puntos de licencia obtenidos exitosamente: {}", puntos);
                    return new LicenciaResponse(true,
                        "Consulta exitosa", puntos, cedula, placa, false);
                }

                // Si no se encontraron datos, puede ser que no haya citaciones
                log.warn("No se encontraron datos de puntos para la cédula: {}", cedula);
                return new LicenciaResponse(true,
                    "No se encontraron citaciones. El conductor tiene 30 puntos (sin infracciones)",
                    30, cedula, placa, false);

            } catch (Exception e) {
                log.error("Error en intento {} al consultar ANT: {}", intento, e.getMessage());

                if (intento < MAX_REINTENTOS) {
                    try {
                        log.info("Esperando {} ms antes del siguiente intento...", TIEMPO_ESPERA_MS);
                        Thread.sleep(TIEMPO_ESPERA_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    // Último intento falló
                    log.error("Todos los intentos fallaron al consultar la ANT");
                    return new LicenciaResponse(false,
                        "No se pudo consultar la ANT después de " + MAX_REINTENTOS + " intentos. " +
                        "El servicio de la ANT tiene baja disponibilidad. Por favor intente más tarde.",
                        null, cedula, placa, false);
                }
            }
        }

        return new LicenciaResponse(false,
            "Error al consultar puntos de licencia", null, cedula, placa, false);
    }

    /**
     * Extrae los puntos de la licencia del documento HTML
     */
    private Integer extraerPuntos(Document doc) {
        try {
            // Buscar en tablas
            Elements tables = doc.select("table");
            for (Element table : tables) {
                Elements rows = table.select("tr");
                for (Element row : rows) {
                    Elements cells = row.select("td, th");
                    for (int i = 0; i < cells.size(); i++) {
                        String cellText = cells.get(i).text().toLowerCase();
                        // Buscar campos relacionados con puntos
                        if (cellText.contains("puntos") || cellText.contains("punto")) {
                            // El valor puede estar en la misma celda o en la siguiente
                            if (i + 1 < cells.size()) {
                                String valorText = cells.get(i + 1).text().trim();
                                Integer puntos = extraerNumero(valorText);
                                if (puntos != null) {
                                    return puntos;
                                }
                            }
                            // O en la misma celda después de dos puntos
                            if (cellText.contains(":")) {
                                String[] parts = cells.get(i).text().split(":");
                                if (parts.length > 1) {
                                    Integer puntos = extraerNumero(parts[1].trim());
                                    if (puntos != null) {
                                        return puntos;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Buscar en spans o divs con clases específicas
            Elements puntosElements = doc.select("span:contains(puntos), div:contains(puntos)");
            for (Element element : puntosElements) {
                String text = element.text();
                Integer puntos = extraerNumero(text);
                if (puntos != null) {
                    return puntos;
                }
            }

            return null;

        } catch (Exception e) {
            log.error("Error al extraer puntos del documento: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extrae un número de un texto
     */
    private Integer extraerNumero(String text) {
        try {
            // Buscar números en el texto
            String numeros = text.replaceAll("[^0-9]", "");
            if (!numeros.isEmpty()) {
                int valor = Integer.parseInt(numeros);
                // Los puntos de licencia están entre 0 y 30
                if (valor >= 0 && valor <= 30) {
                    return valor;
                }
            }
        } catch (NumberFormatException e) {
            // Ignorar errores de parsing
        }
        return null;
    }
}

