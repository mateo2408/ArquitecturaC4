# Guía para Crear Diagramas C4 en IcePanel.io

## 📋 Requisitos
- Cuenta en [icepanel.io](https://icepanel.io)
- Acceso al repositorio GitHub del proyecto

## 🎯 Objetivos
Crear los siguientes diagramas C4:
1. **Diagrama de Contexto** - Vista de alto nivel del sistema
2. **Diagrama de Contenedores** - Aplicaciones y servicios
3. **Diagrama de Componentes** - Componentes internos del backend

---

## 1️⃣ Diagrama de Contexto (C4 Level 1)

### Elementos del Sistema:
- **Usuario**: Persona que consulta información
- **Sistema Web SRI/ANT**: Nuestra aplicación
- **SRI (Servicio de Rentas Internas)**: Sistema externo
- **ANT (Agencia Nacional de Tránsito)**: Sistema externo

### Relaciones:
```
Usuario -> Sistema Web: Ingresa datos y consulta información
Sistema Web -> SRI: Verifica contribuyente y vehículo
Sistema Web -> ANT: Consulta puntos de licencia
```

### Descripción para IcePanel:
- **Nombre**: Sistema de Consultas SRI y ANT
- **Descripción**: Permite a los usuarios consultar información de contribuyentes, vehículos y puntos de licencia de conducir
- **Sistemas Externos**:
  - SRI: Proporciona información de contribuyentes y vehículos
  - ANT: Proporciona información de puntos de licencia

---

## 2️⃣ Diagrama de Contenedores (C4 Level 2)

### Contenedores del Sistema:

#### 1. **Frontend React (SPA)**
- **Tecnología**: React 18 + Vite
- **Puerto**: 5173
- **Responsabilidad**: Interfaz de usuario web
- **Descripción**: Single Page Application que proporciona formularios para ingresar datos y visualizar resultados

#### 2. **Backend API (Spring Boot)**
- **Tecnología**: Java 17 + Spring Boot 3.5.7
- **Puerto**: 8080
- **Responsabilidad**: Lógica de negocio y orquestación
- **Endpoints**:
  - POST /api/verificar-contribuyente
  - POST /api/consultar-vehiculo
  - POST /api/consultar-licencia
  - GET /api/health

#### 3. **Cache Redis**
- **Tecnología**: Redis (Cloud o Local)
- **Puerto**: 6379
- **Responsabilidad**: Almacenamiento en caché
- **TTL**: 24 horas
- **Propósito**: Reducir carga en APIs externas, especialmente ANT (baja disponibilidad)

### Relaciones entre Contenedores:
```
Usuario -> Frontend React: Usa navegador web
Frontend React -> Backend API: Hace llamadas REST API (HTTPS)
Backend API -> Redis Cache: Lee/Escribe datos cacheados
Backend API -> API SRI: Consulta contribuyentes y vehículos (HTTPS)
Backend API -> Web ANT: Web scraping con Jsoup (HTTPS)
```

---

## 3️⃣ Diagrama de Componentes - Backend (C4 Level 3)

### Componentes del Backend:

#### Capa de Controladores:
1. **ConsultaController**
   - Endpoints REST
   - Validación de requests
   - Manejo de respuestas HTTP

#### Capa de Servicios:
2. **SriService**
   - Verificación de contribuyentes
   - Obtención de datos del SRI
   - Cache de respuestas

3. **VehiculoService**
   - Consulta de vehículos
   - Cache de resultados

4. **AntService**
   - Web scraping con Jsoup
   - Reintentos (3 intentos, 5s espera)
   - Cache extendido (24h)

#### Capa de Configuración:
5. **CacheConfig**
   - Configuración de Redis
   - TTL por defecto: 24 horas

6. **CorsConfig**
   - Permite llamadas desde frontend local

#### Capa de DTOs:
7. **Request DTOs**:
   - ConsultaInicialRequest
   - VehiculoRequest
   - LicenciaRequest

8. **Response DTOs**:
   - ContribuyenteResponse
   - VehiculoResponse
   - LicenciaResponse

#### Manejo de Errores:
9. **GlobalExceptionHandler**
   - Manejo centralizado de excepciones

### Flujo de Componentes:
```
ConsultaController -> SriService -> API SRI
                   -> VehiculoService -> API SRI
                   -> AntService -> Web ANT
                   
Todos los servicios -> CacheConfig -> Redis
```

---

## 🚀 Pasos para Crear en IcePanel.io

### Paso 1: Crear Proyecto
1. Ir a [icepanel.io](https://icepanel.io)
2. Crear nuevo proyecto: "Sistema de Consultas SRI y ANT"
3. Seleccionar "C4 Model"

### Paso 2: Conectar con GitHub
1. En IcePanel, ir a Settings -> Integrations
2. Conectar con GitHub
3. Seleccionar el repositorio del proyecto
4. Esto permitirá sincronizar el código con los diagramas

### Paso 3: Crear Diagrama de Contexto
1. Crear nuevo diagrama: "Context"
2. Agregar:
   - Person: "Usuario"
   - System: "Sistema Web SRI/ANT" (nuestro sistema)
   - External System: "SRI"
   - External System: "ANT"
3. Dibujar relaciones con flechas
4. Agregar descripciones a cada relación

### Paso 4: Crear Diagrama de Contenedores
1. Seleccionar "Sistema Web SRI/ANT"
2. Crear nuevo diagrama: "Containers"
3. Agregar contenedores:
   - Container (Web): "Frontend React"
   - Container (API): "Backend Spring Boot"
   - Container (Database): "Redis Cache"
4. Agregar sistemas externos:
   - External System: "API SRI"
   - External System: "Web ANT"
5. Dibujar todas las relaciones

### Paso 5: Crear Diagrama de Componentes
1. Seleccionar "Backend Spring Boot"
2. Crear nuevo diagrama: "Components"
3. Agregar componentes organizados por capas:
   - **Controller Layer**: ConsultaController
   - **Service Layer**: SriService, VehiculoService, AntService
   - **Config Layer**: CacheConfig, CorsConfig
   - **Exception Layer**: GlobalExceptionHandler
4. Dibujar relaciones entre componentes

### Paso 6: Agregar Detalles
Para cada elemento:
- **Nombre claro y descriptivo**
- **Tecnología utilizada**
- **Responsabilidad principal**
- **Puertos (si aplica)**

### Paso 7: Documentar Patrones
En las notas del proyecto, documentar:
- **Patrón de Cache**: Para manejar baja disponibilidad de ANT
- **Patrón de Reintentos**: 3 intentos con 5 segundos de espera
- **Validación**: Jakarta Validation en DTOs
- **Web Scraping**: Jsoup para ANT (no tiene API REST)

---

## 📊 Elementos Clave a Destacar

### Decisiones Arquitectónicas:
1. **Cache Redis**: Elegido por baja disponibilidad de ANT
2. **Web Scraping**: Necesario porque ANT no tiene API pública
3. **Patrón de Reintentos**: Mejora confiabilidad
4. **SPA React**: Mejor experiencia de usuario
5. **Validación en Backend**: Seguridad y consistencia

### Tecnologías Clave:
- **Frontend**: React 18, Axios, Vite
- **Backend**: Spring Boot 3.5.7, Java 17
- **Cache**: Redis (Cloud compatible)
- **Web Scraping**: Jsoup 1.16.1
- **Validación**: Jakarta Validation

---

## 🔗 Enlace con GitHub

Una vez creados los diagramas en IcePanel:
1. Ir a Settings -> GitHub Integration
2. Mapear componentes del diagrama con carpetas del código:
   - `ConsultaController` -> `/src/main/java/org/example/arquitecturac4/controller`
   - `SriService` -> `/src/main/java/org/example/arquitecturac4/service`
   - etc.
3. IcePanel mostrará el código relacionado junto a cada componente

---

## 📸 Capturas Recomendadas

Para documentación final, exportar:
1. Diagrama de Contexto en PNG/SVG
2. Diagrama de Contenedores en PNG/SVG
3. Diagrama de Componentes en PNG/SVG
4. Agregar al README.md del proyecto

---

## 💡 Tips Adicionales

1. **Colores**: Usar colores consistentes
   - Verde: Nuestros componentes
   - Gris: Sistemas externos
   - Azul: Almacenamiento/Cache

2. **Leyenda**: Agregar leyenda explicando:
   - Línea sólida: Sincrónico
   - Línea punteada: Asincrónico (si aplica)
   - Grosor: Frecuencia de uso

3. **Notas**: Agregar notas sobre:
   - Limitaciones conocidas (disponibilidad ANT)
   - Decisiones de diseño
   - Próximas mejoras

---

## 🎓 Recursos Adicionales

- [C4 Model](https://c4model.com/)
- [IcePanel Documentation](https://docs.icepanel.io/)
- [Spring Boot Best Practices](https://spring.io/guides)
- [Redis Caching Strategies](https://redis.io/docs/manual/client-side-caching/)

