# 🚀 Guía de Despliegue - Sistema SRI/ANT

## 📋 Opciones de Despliegue

### Opción 1: Despliegue Local (Desarrollo)
### Opción 2: Despliegue en Cloud (Producción)

---

## 🖥️ OPCIÓN 1: DESPLIEGUE LOCAL

### Requisitos:
- Java 17+
- Maven 3.6+
- Node.js 16+
- Redis

### Paso 1: Instalar Redis

#### macOS:
```bash
brew install redis
brew services start redis
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install redis-server
sudo systemctl start redis
sudo systemctl enable redis
```

#### Windows:
1. Descargar Redis desde: https://github.com/microsoftarchive/redis/releases
2. Ejecutar `redis-server.exe`

### Paso 2: Backend

```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

**Verificar**: http://localhost:8080/api/health

### Paso 3: Frontend

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev
```

**Verificar**: http://localhost:5173

---

## ☁️ OPCIÓN 2: DESPLIEGUE EN CLOUD

### A) Backend en AWS Elastic Beanstalk

#### 1. Preparar el JAR:
```bash
mvn clean package
```
Esto genera: `target/ArquitecturaC4-0.0.1-SNAPSHOT.jar`

#### 2. Crear aplicación en Elastic Beanstalk:
```bash
# Instalar AWS CLI
brew install awscli

# Configurar credenciales
aws configure

# Crear aplicación
eb init -p java-17 arquitectura-c4

# Crear entorno y desplegar
eb create arquitectura-c4-env
eb deploy
```

#### 3. Configurar Redis en AWS ElastiCache:
1. Ir a AWS Console → ElastiCache
2. Crear cluster Redis
3. Copiar endpoint (ejemplo: `arquitectura-c4.cache.amazonaws.com:6379`)
4. Actualizar `application.properties`:
```properties
spring.data.redis.host=arquitectura-c4.cache.amazonaws.com
spring.data.redis.port=6379
```

#### 4. Variables de entorno en Elastic Beanstalk:
```bash
eb setenv SPRING_DATA_REDIS_HOST=arquitectura-c4.cache.amazonaws.com
eb setenv SPRING_DATA_REDIS_PORT=6379
eb setenv SERVER_PORT=5000
```

---

### B) Backend en Heroku

#### 1. Crear archivo `Procfile`:
```bash
echo "web: java -jar target/ArquitecturaC4-0.0.1-SNAPSHOT.jar" > Procfile
```

#### 2. Crear archivo `system.properties`:
```properties
java.runtime.version=17
```

#### 3. Desplegar:
```bash
# Instalar Heroku CLI
brew tap heroku/brew && brew install heroku

# Login
heroku login

# Crear aplicación
heroku create arquitectura-c4-backend

# Agregar Redis addon
heroku addons:create heroku-redis:mini

# Configurar variables
heroku config:set SPRING_PROFILES_ACTIVE=prod

# Desplegar
git push heroku main
```

---

### C) Backend en Azure App Service

#### 1. Crear App Service:
```bash
# Instalar Azure CLI
brew install azure-cli

# Login
az login

# Crear grupo de recursos
az group create --name arquitectura-c4-rg --location eastus

# Crear App Service
az webapp create \
  --resource-group arquitectura-c4-rg \
  --name arquitectura-c4-backend \
  --runtime "JAVA:17-java17"

# Crear Redis en Azure
az redis create \
  --resource-group arquitectura-c4-rg \
  --name arquitectura-c4-cache \
  --location eastus \
  --sku Basic \
  --vm-size c0
```

#### 2. Desplegar:
```bash
# Empaquetar
mvn clean package

# Desplegar
az webapp deploy \
  --resource-group arquitectura-c4-rg \
  --name arquitectura-c4-backend \
  --src-path target/ArquitecturaC4-0.0.1-SNAPSHOT.jar
```

---

### D) Frontend en Vercel

#### 1. Preparar frontend:
```bash
cd frontend

# Actualizar API_URL para producción
# Editar src/App.jsx:
const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api'
```

#### 2. Crear archivo `.env.production`:
```env
VITE_API_URL=https://arquitectura-c4-backend.herokuapp.com/api
```

#### 3. Desplegar en Vercel:
```bash
# Instalar Vercel CLI
npm install -g vercel

# Login
vercel login

# Desplegar
vercel --prod
```

#### 4. Configurar variables de entorno en Vercel:
- Ir a: https://vercel.com/dashboard
- Seleccionar proyecto
- Settings → Environment Variables
- Agregar: `VITE_API_URL` con la URL del backend

---

### E) Frontend en Netlify

#### 1. Crear archivo `netlify.toml`:
```toml
[build]
  command = "npm run build"
  publish = "dist"

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200

[build.environment]
  VITE_API_URL = "https://arquitectura-c4-backend.herokuapp.com/api"
```

#### 2. Desplegar:
```bash
# Instalar Netlify CLI
npm install -g netlify-cli

# Login
netlify login

# Desplegar
netlify deploy --prod
```

---

## 🐳 OPCIÓN 3: DESPLIEGUE CON DOCKER

### 1. Crear Dockerfile para Backend:
```dockerfile
# Archivo: Dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/ArquitecturaC4-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Crear Dockerfile para Frontend:
```dockerfile
# Archivo: frontend/Dockerfile
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### 3. Crear nginx.conf para Frontend:
```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 4. Crear docker-compose.yml:
```yaml
version: '3.8'

services:
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    command: redis-server --appendonly yes

  backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
    depends_on:
      - redis

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  redis-data:
```

### 5. Ejecutar con Docker Compose:
```bash
# Construir y ejecutar
docker-compose up -d

# Ver logs
docker-compose logs -f

# Detener
docker-compose down
```

---

## 🔐 CONFIGURACIÓN DE SEGURIDAD

### 1. Variables de Entorno de Producción:
```properties
# application-prod.properties
spring.data.redis.host=${REDIS_HOST}
spring.data.redis.port=${REDIS_PORT}
spring.data.redis.password=${REDIS_PASSWORD}

# Deshabilitar CORS en producción o configurar dominios específicos
# CorsConfig.java - Modificar:
.allowedOrigins("https://tu-dominio-frontend.com")
```

### 2. HTTPS:
- Usar certificado SSL (Let's Encrypt gratuito)
- Configurar en Nginx/Apache o usar el de la plataforma cloud

### 3. Rate Limiting:
Agregar en `application.properties`:
```properties
spring.cloud.gateway.routes[0].filters[0]=RateLimiter
```

### 4. API Keys (Opcional):
Implementar autenticación con Spring Security si es necesario.

---

## 📊 MONITOREO Y LOGS

### 1. Logs en Producción:
```properties
# application-prod.properties
logging.level.org.example.arquitecturac4=INFO
logging.file.name=/var/log/arquitectura-c4/application.log
logging.file.max-size=10MB
logging.file.max-history=10
```

### 2. Métricas con Spring Boot Actuator:
```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
# application.properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=always
```

### 3. Monitoreo con Prometheus + Grafana (Opcional):
```yaml
# docker-compose.yml - Agregar:
  prometheus:
    image: prom/prometheus
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"

  grafana:
    image: grafana/grafana
    ports:
      - "3000:3000"
```

---

## ✅ CHECKLIST DE DESPLIEGUE

### Pre-Despliegue:
- [ ] Código compilado sin errores: `mvn clean compile`
- [ ] Tests pasando (si existen): `mvn test`
- [ ] Variables de entorno configuradas
- [ ] Redis configurado y accesible
- [ ] CORS configurado correctamente

### Post-Despliegue:
- [ ] Backend responde: `/api/health`
- [ ] Frontend carga correctamente
- [ ] Integración frontend-backend funciona
- [ ] Cache Redis funciona
- [ ] Logs sin errores críticos
- [ ] Consultas a SRI funcionan
- [ ] Consultas a ANT funcionan (con reintentos)

---

## 🆘 TROUBLESHOOTING

### Problema: Backend no se conecta a Redis
**Solución**:
```bash
# Verificar que Redis esté corriendo
redis-cli ping
# Debe responder: PONG

# Ver logs de Redis
redis-cli monitor
```

### Problema: CORS Error en Frontend
**Solución**:
1. Verificar que `CorsConfig.java` incluya la URL del frontend
2. Verificar headers en DevTools del navegador
3. Agregar dominio en `allowedOrigins()`

### Problema: Timeout en consultas ANT
**Solución**:
- Aumentar timeout en `AntService.java`:
```java
private static final int TIMEOUT_MS = 20000; // 20 segundos
```
- Verificar que el cache esté funcionando

### Problema: JAR no ejecuta
**Solución**:
```bash
# Verificar versión de Java
java -version

# Ejecutar con más memoria
java -Xmx512m -jar target/ArquitecturaC4-0.0.1-SNAPSHOT.jar
```

---

## 📞 SOPORTE Y RECURSOS

### Documentación:
- Spring Boot: https://spring.io/guides
- React: https://react.dev
- Redis: https://redis.io/docs
- Docker: https://docs.docker.com

### Monitoreo de APIs Externas:
- SRI Status: Verificar manualmente la disponibilidad
- ANT Status: Monitorear logs de reintentos

---

## 🎓 MEJORES PRÁCTICAS

1. **Usar perfiles de Spring**: `dev`, `test`, `prod`
2. **Separar configuraciones** por entorno
3. **Monitorear logs** regularmente
4. **Hacer backups** de configuración
5. **Documentar cambios** en el README
6. **Usar CI/CD** (GitHub Actions, Jenkins)

---

**Última actualización**: 19 de Noviembre de 2025  
**Versión**: 1.0.0

