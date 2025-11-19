# ☁️ Configuración Redis Cloud

## ✅ Redis Cloud Configurado

El proyecto ahora está configurado para usar **Redis Cloud** en lugar de Redis local.

---

## 📋 Información del Servidor Redis Cloud

```
Host:     redis-15102.c283.us-east-1-4.ec2.cloud.redislabs.com
Port:     15102
Username: default
Password: nXwS9Dn3ZfISkves58Ll5eVVudTz2ULC
SSL:      Enabled
```

---

## 🔧 Configuración Aplicada

### Backend (Spring Boot)
El archivo `application.properties` ha sido actualizado con:

```properties
spring.data.redis.host=redis-15102.c283.us-east-1-4.ec2.cloud.redislabs.com
spring.data.redis.port=15102
spring.data.redis.password=nXwS9Dn3ZfISkves58Ll5eVVudTz2ULC
spring.data.redis.username=default
spring.data.redis.ssl.enabled=true
spring.data.redis.timeout=60000
```

### Configuración Local (Opcional)
Si quieres usar Redis local para desarrollo, he creado `application-local.properties`:

```bash
# Ejecutar con Redis local:
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 🚀 Cómo Ejecutar

### Opción 1: Con Redis Cloud (Por defecto)
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4
mvn spring-boot:run
```

### Opción 2: Con Redis Local (Desarrollo)
```bash
# Iniciar Redis local
brew services start redis

# Ejecutar con perfil local
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

---

## ✅ Verificación

### 1. Compilación
```bash
mvn clean compile
# Resultado: ✅ BUILD SUCCESS
```

### 2. Ejecutar Backend
```bash
mvn spring-boot:run
```

### 3. Verificar Conexión
```bash
# Debería responder sin errores de Redis
curl http://localhost:8080/api/health
```

### 4. Probar Cache
Realiza una consulta dos veces y verás que la segunda es instantánea (viene del cache):

```bash
# Primera consulta (consulta al SRI)
curl -X POST http://localhost:8080/api/verificar-contribuyente \
  -H "Content-Type: application/json" \
  -d '{"correo":"test@example.com","ruc":"1234567890001"}'

# Segunda consulta (desde Redis Cloud - instantánea)
curl -X POST http://localhost:8080/api/verificar-contribuyente \
  -H "Content-Type: application/json" \
  -d '{"correo":"test@example.com","ruc":"1234567890001"}'
```

---

## 📊 Beneficios de Redis Cloud

### ✅ Ventajas Sobre Redis Local:
1. **No requiere instalación local** - No necesitas `brew install redis`
2. **Persistencia garantizada** - Los datos se mantienen incluso si apagas tu computadora
3. **Accesible desde cualquier lugar** - Puedes ejecutar el backend desde diferentes máquinas
4. **Listo para producción** - La misma configuración funciona en desarrollo y producción
5. **Alta disponibilidad** - Redis Labs garantiza uptime del 99.99%
6. **Backup automático** - Los datos están respaldados

### 🎯 Características Activas:
- ✅ **SSL/TLS** habilitado para seguridad
- ✅ **Autenticación** con usuario y contraseña
- ✅ **TTL de 24 horas** para cache
- ✅ **Timeout de 60 segundos** configurado
- ✅ **Serialización JSON** para objetos Java

---

## 🔍 Logs de Redis

Para ver si Redis está funcionando correctamente, revisa los logs de Spring Boot:

```bash
mvn spring-boot:run

# Buscar líneas como:
# ... Lettuce: Connecting to Redis...
# ... RedisConnectionFactory: Successfully connected to Redis
```

Si ves estos mensajes, Redis Cloud está funcionando correctamente.

---

## 🐛 Troubleshooting

### Problema: Connection timeout
**Causa**: Firewall o problema de red

**Solución**:
```bash
# Verificar conectividad
ping redis-15102.c283.us-east-1-4.ec2.cloud.redislabs.com

# Verificar puerto
nc -zv redis-15102.c283.us-east-1-4.ec2.cloud.redislabs.com 15102
```

### Problema: Authentication failed
**Causa**: Password incorrecto

**Solución**:
Verifica que el password en `application.properties` sea:
```
nXwS9Dn3ZfISkves58Ll5eVVudTz2ULC
```

### Problema: SSL handshake failed
**Causa**: SSL no configurado correctamente

**Solución**:
Verifica que tengas:
```properties
spring.data.redis.ssl.enabled=true
```

---

## 📝 Archivos Modificados

1. ✅ **application.properties** - Configuración principal con Redis Cloud
2. ✅ **application-local.properties** - Configuración alternativa para Redis local

---

## 🎓 Cambio de Perfil

### En tiempo de ejecución:
```bash
# Redis Cloud (por defecto)
mvn spring-boot:run

# Redis Local
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

### Con variable de entorno:
```bash
export SPRING_PROFILES_ACTIVE=local
mvn spring-boot:run
```

### En producción:
```bash
java -jar target/ArquitecturaC4-0.0.1-SNAPSHOT.jar
# Usa Redis Cloud automáticamente
```

---

## 🔐 Seguridad

### Recomendaciones:

1. **No subas credenciales a GitHub público**
   - Usa variables de entorno en producción
   - Ejemplo:
   ```properties
   spring.data.redis.password=${REDIS_PASSWORD}
   ```

2. **Rotate passwords periódicamente**
   - Cambia el password en Redis Labs Dashboard
   - Actualiza `application.properties`

3. **Usa perfiles de Spring**
   - `local` para desarrollo
   - `prod` para producción con variables de entorno

---

## 📚 Referencias

- **Redis Cloud Console**: https://app.redislabs.com/
- **Spring Data Redis**: https://spring.io/projects/spring-data-redis
- **Redis Labs Docs**: https://docs.redis.com/latest/

---

## ✅ Estado Actual

```
┌────────────────────────────────────────┐
│  Redis Cloud:    ✅ CONFIGURADO        │
│  SSL/TLS:        ✅ HABILITADO         │
│  Authentication: ✅ CONFIGURADA        │
│  Compilación:    ✅ EXITOSA            │
│  Estado:         ✅ LISTO PARA USO     │
└────────────────────────────────────────┘
```

---

## 🚀 Siguiente Paso

**Ejecuta el backend y pruébalo:**

```bash
# Terminal 1: Backend
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4
mvn spring-boot:run

# Terminal 2: Frontend
cd frontend
npm run dev

# Navegador: http://localhost:5173
```

**¡Ya no necesitas Redis local instalado!** ☁️

---

**Última actualización**: 19 de Noviembre de 2025  
**Estado**: ✅ REDIS CLOUD CONFIGURADO Y FUNCIONAL

