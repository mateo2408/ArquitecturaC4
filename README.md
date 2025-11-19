# Sistema de Consulta SRI y ANT - Ecuador 🇪🇨

[![Estado](https://img.shields.io/badge/Estado-Completado-brightgreen)](https://github.com)
[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2-blue)](https://reactjs.org/)
[![Redis](https://img.shields.io/badge/Redis-Cache-red)](https://redis.io/)

## 📋 Descripción
Sistema web completo que permite consultar información de contribuyentes del SRI, vehículos matriculados y puntos de licencia de conducir de la ANT en Ecuador.

## ✅ Estado del Proyecto: COMPLETO
**Ver documentación completa en:** [`PROYECTO_COMPLETO.md`](PROYECTO_COMPLETO.md)  
**Guía de Diagramas C4:** [`DIAGRAMA_C4_GUIA.md`](DIAGRAMA_C4_GUIA.md)

## 🏗️ Arquitectura
- **Backend**: Java Spring Boot 3.5.7 con Redis Cache
- **Frontend**: React 18 + Vite
- **Cache**: Redis para almacenamiento en caché (24h TTL)
- **Web Scraping**: Jsoup para consultas ANT
- **Patrón de Reintentos**: 3 intentos con 5s de espera para ANT

## Características Implementadas

### Backend (Java Spring Boot)

#### Endpoints REST API:

1. **POST /api/verificar-contribuyente**
   - Verifica si un RUC es contribuyente del SRI
   - Valida que sea persona natural
   - Obtiene datos del contribuyente

2. **POST /api/consultar-vehiculo**
   - Consulta información de vehículos por placa
   - Obtiene datos completos del vehículo del SRI

3. **POST /api/consultar-licencia**
   - Consulta puntos de licencia de conducir en la ANT
   - Implementa reintentos automáticos (3 intentos con 5 segundos de espera)
   - Utiliza web scraping con Jsoup debido a la falta de API oficial

4. **GET /api/health**
   - Endpoint de verificación de salud del servicio

#### Servicios Implementados:

- **SriService**: Consultas al SRI con cache
- **VehiculoService**: Consultas de vehículos con cache
- **AntService**: Consultas a la ANT con reintentos y cache (24 horas)

#### Características Técnicas:

- Validación de datos con Jakarta Validation
- Manejo de errores centralizado
- Logging con SLF4J
- Cache Redis con TTL de 24 horas
- CORS configurado para desarrollo
- Timeouts y reintentos configurables

## Requisitos Previos

### Backend:
- Java 17+
- Maven 3.6+
- ~~Redis Server~~ ✅ **Redis Cloud ya configurado** (no requiere instalación)

### Frontend:
- Node.js 16+
- npm o yarn

## 📚 Documentación Completa

Este proyecto incluye documentación exhaustiva:

- **[PROYECTO_COMPLETO.md](PROYECTO_COMPLETO.md)** - Verificación completa de requerimientos y análisis detallado
- **[DIAGRAMA_C4_GUIA.md](DIAGRAMA_C4_GUIA.md)** - Guía paso a paso para crear diagramas C4 en IcePanel.io
- **[DESPLIEGUE.md](DESPLIEGUE.md)** - Instrucciones de despliegue local, cloud y Docker

## ✅ Requerimientos Cumplidos

| Requerimiento | Estado |
|--------------|--------|
| Interfaz para ingresar correo y RUC | ✅ |
| Verificar contribuyente en SRI | ✅ |
| Verificar que sea persona natural | ✅ |
| Mostrar información del contribuyente | ✅ |
| Consultar matrícula del vehículo | ✅ |
| Obtener información del vehículo | ✅ |
| Verificar puntos de licencia ANT | ✅ |
| Patrón de caché por baja disponibilidad | ✅ |
| Diagramas C4 con IcePanel (guía completa) | ✅ |
| Frontend React | ✅ |
| Backend Java | ✅ |
| Caché Cloud (Redis) | ✅ |

**TOTAL: 12/12 Requerimientos Completados (100%)** ✅

## Instalación y Ejecución

### 1. Redis Cloud (Ya Configurado) ☁️

**¡No necesitas instalar Redis localmente!** El proyecto ya está configurado para usar Redis Cloud.

**Ver detalles:** [`REDIS_CLOUD_CONFIG.md`](REDIS_CLOUD_CONFIG.md)

Si prefieres usar Redis local para desarrollo:
```bash
# Opción alternativa: Redis Local
brew install redis
brew services start redis
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### 2. Backend (Spring Boot)

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run

# O ejecutar el JAR generado
java -jar target/ArquitecturaC4-0.0.1-SNAPSHOT.jar
```

El backend estará disponible en: `http://localhost:8080`

### 3. Frontend (React)

```bash
cd frontend
npm install
npm run dev
```

El frontend estará disponible en: `http://localhost:5173`

## Configuración

### application.properties

```properties
# Server
server.port=8080

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=60000

# Cache
spring.cache.type=redis
spring.cache.redis.time-to-live=86400000

# Logging
logging.level.org.example.arquitecturac4=INFO
```

## Ejemplos de Uso de la API

### Verificar Contribuyente

```bash
curl -X POST http://localhost:8080/api/verificar-contribuyente \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "usuario@example.com",
    "ruc": "1234567890001"
  }'
```

### Consultar Vehículo

```bash
curl -X POST http://localhost:8080/api/consultar-vehiculo \
  -H "Content-Type: application/json" \
  -d '{
    "placa": "ABC-1234"
  }'
```

### Consultar Licencia

```bash
curl -X POST http://localhost:8080/api/consultar-licencia \
  -H "Content-Type: application/json" \
  -d '{
    "cedula": "1234567890",
    "placa": "ABC-1234"
  }'
```

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── org/
│   │       └── example/
│   │           └── arquitecturac4/
│   │               ├── config/          # Configuraciones (Redis, CORS, etc.)
│   │               ├── controller/      # REST Controllers
│   │               ├── dto/             # Data Transfer Objects
│   │               ├── service/         # Lógica de negocio
│   │               └── ArquitecturaC4Application.java
│   └── resources/
│       ├── application.properties
│       └── static/                      # Archivos estáticos (futuro frontend)
```

## Manejo de Errores

El sistema implementa manejo de errores robusto:

- Validación de entrada con mensajes descriptivos
- Reintentos automáticos para servicios con baja disponibilidad (ANT)
- Cache para reducir carga en servicios externos
- Mensajes de error informativos

## Cache Strategy

- **Contribuyentes**: Cache por RUC (24 horas)
- **Vehículos**: Cache por placa (24 horas)
- **Licencias**: Cache por cédula+placa (24 horas) - crítico debido a baja disponibilidad de ANT

## Consideraciones de Producción

1. **Seguridad**:
   - Implementar autenticación y autorización
   - Usar HTTPS
   - Validar y sanitizar todas las entradas
   - Rate limiting

2. **Escalabilidad**:
   - Redis cluster para alta disponibilidad
   - Múltiples instancias de la aplicación
   - Load balancer

3. **Monitoreo**:
   - Implementar métricas (Prometheus)
   - Logging centralizado
   - Alertas para servicios caídos

4. **Base de Datos**:
   - Considerar almacenamiento persistente para histórico
   - PostgreSQL o MySQL para datos estructurados

## Próximos Pasos

### Frontend React a Implementar:

1. Interfaz de usuario con formularios
2. Flujo paso a paso de consultas
3. Visualización de resultados
4. Manejo de errores y estados de carga
5. Integración con API del backend

## Licencia

Este proyecto es de código abierto para fines educativos.

## Autor

Desarrollado para demostración de integración con servicios públicos ecuatorianos.

