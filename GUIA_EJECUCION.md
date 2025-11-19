# 🚀 GUÍA PASO A PASO - Ejecutar la Aplicación

## 📋 Requisitos Previos

Antes de empezar, verifica que tienes instalado:

### ✅ Verificar Java
```bash
java -version
# Debe mostrar: Java 17 o superior
```

Si no tienes Java 17:
```bash
# macOS (con Homebrew)
brew install openjdk@17
```

### ✅ Verificar Maven
```bash
mvn -version
# Debe mostrar: Apache Maven 3.6+
```

Si no tienes Maven:
```bash
# macOS (con Homebrew)
brew install maven
```

### ✅ Verificar Node.js
```bash
node -version
# Debe mostrar: v16 o superior
```

Si no tienes Node.js:
```bash
# macOS (con Homebrew)
brew install node
```

---

## 🎯 PASOS PARA EJECUTAR

### PASO 1: Abrir Terminal

Abre la aplicación **Terminal** en tu Mac.

---

### PASO 2: Navegar al Proyecto

```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4
```

---

### PASO 3: Compilar el Backend (Primera vez)

```bash
mvn clean compile
```

**Espera a que aparezca:** `BUILD SUCCESS`

⏱️ Tiempo estimado: 30-60 segundos

**Si hay errores:**
- Verifica que estés en la carpeta correcta
- Verifica que Java 17 esté instalado
- Ejecuta: `mvn clean install -U`

---

### PASO 4: Iniciar el Backend (Spring Boot)

#### Opción A: Con Maven (Recomendado para desarrollo)
```bash
mvn spring-boot:run
```

#### Opción B: Con JAR (Alternativa)
```bash
# Primero generar el JAR
mvn clean package -DskipTests

# Luego ejecutarlo
java -jar target/ArquitecturaC4-0.0.1-SNAPSHOT.jar
```

**Espera hasta ver:**
```
Started ArquitecturaC4Application in X.XXX seconds
```

⏱️ Tiempo estimado: 10-20 segundos

**El backend estará corriendo en:** http://localhost:8080

**✅ Verificar que funciona:**
Abre otra terminal (no cierres la anterior) y ejecuta:
```bash
curl http://localhost:8080/api/health
```

Debe responder:
```json
{"status":"UP","service":"Sistema de Consultas SRI y ANT","version":"1.0.0"}
```

**⚠️ IMPORTANTE:** **NO cierres esta terminal.** Déjala corriendo.

---

### PASO 5: Abrir Nueva Terminal para el Frontend

Abre una **SEGUNDA TERMINAL** (Cmd + T para nueva pestaña).

---

### PASO 6: Navegar a la Carpeta del Frontend

```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4/frontend
```

---

### PASO 7: Instalar Dependencias (Solo primera vez)

```bash
npm install
```

**Espera a que termine.** Verás algo como:
```
added 91 packages in 5s
```

⏱️ Tiempo estimado: 10-30 segundos

**Si hay errores:**
- Verifica que Node.js esté instalado
- Ejecuta: `npm cache clean --force`
- Vuelve a ejecutar: `npm install`

---

### PASO 8: Iniciar el Frontend (React + Vite)

```bash
npm run dev
```

**Espera hasta ver:**
```
VITE v5.0.8  ready in XXX ms

➜  Local:   http://localhost:5173/
➜  Network: use --host to expose
```

⏱️ Tiempo estimado: 2-5 segundos

**El frontend estará corriendo en:** http://localhost:5173

**⚠️ IMPORTANTE:** **NO cierres esta terminal.** Déjala corriendo.

---

### PASO 9: Abrir en el Navegador

Abre tu navegador web favorito (Chrome, Safari, Firefox) y ve a:

```
http://localhost:5173
```

**✅ Deberías ver:** La interfaz principal del sistema con el título:
```
🇪🇨 Consultas SRI y ANT - Ecuador
```

---

## 🎉 ¡LISTO! La aplicación está corriendo

### Ahora tienes:

#### ✅ Terminal 1 - Backend
```
Puerto: 8080
URL: http://localhost:8080
Estado: Corriendo ✓
```

#### ✅ Terminal 2 - Frontend
```
Puerto: 5173
URL: http://localhost:5173
Estado: Corriendo ✓
```

#### ✅ Navegador
```
URL: http://localhost:5173
Estado: Mostrando la aplicación ✓
```

---

## 🧪 PROBAR LA APLICACIÓN

### Paso 1: Ingresar Datos del Contribuyente
1. **Correo:** Ingresa cualquier correo válido (ej: `test@example.com`)
2. **RUC:** Ingresa un RUC de 13 dígitos (ej: `1234567890001`)
3. Click en **"Verificar Contribuyente"**

### Paso 2: Consultar Vehículo
1. **Placa:** Ingresa una placa (ej: `ABC-1234`)
2. Click en continuar

### Paso 3: Consultar Licencia
1. **Cédula:** Se llena automáticamente desde el RUC
2. **Placa:** Se llena automáticamente
3. Click en continuar

### Paso 4: Ver Resultados
Verás un resumen con toda la información consultada.

---

## 🛑 DETENER LA APLICACIÓN

### Para detener el Backend (Terminal 1):
```
Presiona: Ctrl + C
```

### Para detener el Frontend (Terminal 2):
```
Presiona: Ctrl + C
```

---

## 🔄 VOLVER A EJECUTAR

La próxima vez que quieras ejecutar la aplicación:

### Terminal 1 - Backend:
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4
mvn spring-boot:run
```

### Terminal 2 - Frontend:
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4/frontend
npm run dev
```

**Ya NO necesitas:**
- ❌ `mvn clean compile` (solo la primera vez)
- ❌ `npm install` (solo la primera vez o si cambias dependencias)

---

## 🐛 SOLUCIÓN DE PROBLEMAS

### Problema 1: "Puerto 8080 ya está en uso"

**Solución:**
```bash
# Encontrar el proceso que usa el puerto 8080
lsof -i :8080

# Matar el proceso (reemplaza PID con el número que te dio el comando anterior)
kill -9 PID
```

O simplemente reinicia tu computadora.

---

### Problema 2: "Puerto 5173 ya está en uso"

**Solución:**
```bash
# Encontrar el proceso que usa el puerto 5173
lsof -i :5173

# Matar el proceso
kill -9 PID
```

---

### Problema 3: Error "Cannot connect to Redis"

**Solución:**
El proyecto está configurado para usar **Redis Cloud**, así que NO necesitas Redis local.

Si ves este error:
1. Verifica tu conexión a internet
2. El backend se conectará automáticamente a Redis Cloud

---

### Problema 4: "ECONNREFUSED localhost:8080" en el Frontend

**Causa:** El backend no está corriendo.

**Solución:**
1. Verifica que la Terminal 1 (backend) esté corriendo
2. Verifica que muestre: "Started ArquitecturaC4Application"
3. Prueba: `curl http://localhost:8080/api/health`

---

### Problema 5: Página en blanco en el navegador

**Solución:**
1. Abre la consola del navegador (F12 o Cmd + Option + I)
2. Busca errores en rojo
3. Verifica que ambos servidores estén corriendo
4. Prueba refrescar la página (Cmd + R)

---

### Problema 6: "BUILD FAILURE" en Maven

**Solución:**
```bash
# Limpiar todo y volver a compilar
mvn clean install -U

# Si persiste, eliminar carpeta target
rm -rf target
mvn clean install
```

---

### Problema 7: Errores en npm install

**Solución:**
```bash
# Limpiar cache de npm
npm cache clean --force

# Eliminar node_modules y package-lock.json
rm -rf node_modules package-lock.json

# Volver a instalar
npm install
```

---

## 📊 ESTRUCTURA DE EJECUCIÓN

```
┌─────────────────────────────────────────┐
│         TU COMPUTADORA                  │
│                                         │
│  ┌────────────────────────────────┐    │
│  │  Terminal 1 - Backend          │    │
│  │  Puerto: 8080                  │    │
│  │  mvn spring-boot:run           │    │
│  └────────────┬───────────────────┘    │
│               │                         │
│               │ Conectado a             │
│               ▼                         │
│  ┌────────────────────────────────┐    │
│  │  Redis Cloud ☁️                │    │
│  │  (Cache en la nube)            │    │
│  └────────────────────────────────┘    │
│                                         │
│  ┌────────────────────────────────┐    │
│  │  Terminal 2 - Frontend         │    │
│  │  Puerto: 5173                  │    │
│  │  npm run dev                   │    │
│  └────────────┬───────────────────┘    │
│               │                         │
│               │ Llamadas HTTP           │
│               ▼                         │
│  ┌────────────────────────────────┐    │
│  │  Navegador                     │    │
│  │  http://localhost:5173         │    │
│  └────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

---

## 📝 CHECKLIST DE EJECUCIÓN

### Antes de empezar:
- [ ] Java 17+ instalado
- [ ] Maven instalado
- [ ] Node.js instalado
- [ ] Conexión a internet (para Redis Cloud)

### Primera vez:
- [ ] `cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4`
- [ ] `mvn clean compile`
- [ ] `mvn spring-boot:run` (Terminal 1)
- [ ] Abrir nueva terminal (Terminal 2)
- [ ] `cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4/frontend`
- [ ] `npm install`
- [ ] `npm run dev`
- [ ] Abrir navegador en `http://localhost:5173`

### Próximas veces:
- [ ] Terminal 1: `mvn spring-boot:run`
- [ ] Terminal 2: `npm run dev`
- [ ] Abrir navegador en `http://localhost:5173`

---

## 🎯 COMANDOS RÁPIDOS DE REFERENCIA

### Backend:
```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# Generar JAR
mvn clean package -DskipTests

# Verificar salud
curl http://localhost:8080/api/health
```

### Frontend:
```bash
# Instalar dependencias (primera vez)
npm install

# Ejecutar modo desarrollo
npm run dev

# Construir para producción
npm run build
```

### Útiles:
```bash
# Ver qué está usando el puerto 8080
lsof -i :8080

# Ver qué está usando el puerto 5173
lsof -i :5173

# Matar proceso por PID
kill -9 <PID>
```

---

## 🎓 TIPS ADICIONALES

### 1. Atajos de Teclado en Terminal:
- **Ctrl + C** - Detener proceso
- **Cmd + T** - Nueva pestaña
- **Cmd + W** - Cerrar pestaña
- **Cmd + K** - Limpiar pantalla

### 2. Ver logs en tiempo real:
El backend y frontend muestran logs automáticamente en las terminales.

### 3. Hot Reload:
- **Frontend:** Se recarga automáticamente al editar archivos
- **Backend:** Necesitas reiniciar (Ctrl+C y volver a ejecutar)

### 4. Modo producción:
```bash
# Frontend
npm run build
# Genera carpeta dist/ con archivos optimizados

# Backend
mvn clean package -DskipTests
# Genera JAR en target/
```

---

## 📚 DOCUMENTACIÓN ADICIONAL

- **REDIS_CLOUD_CONFIG.md** - Configuración de Redis Cloud
- **PROYECTO_COMPLETO.md** - Análisis completo del proyecto
- **DESPLIEGUE.md** - Cómo desplegar en producción
- **README.md** - Documentación general

---

## ✅ VERIFICACIÓN FINAL

Si todo está correcto, deberías tener:

1. ✅ **Terminal 1** mostrando:
   ```
   Started ArquitecturaC4Application in X.XXX seconds
   ```

2. ✅ **Terminal 2** mostrando:
   ```
   ➜  Local:   http://localhost:5173/
   ```

3. ✅ **Navegador** mostrando la interfaz de la aplicación

4. ✅ **Puedes hacer consultas** y ver resultados

---

## 🎉 ¡ÉXITO!

**Tu aplicación está corriendo correctamente.**

### URLs importantes:
- **Frontend:** http://localhost:5173
- **Backend API:** http://localhost:8080/api
- **Health Check:** http://localhost:8080/api/health

### Endpoints disponibles:
- `POST /api/verificar-contribuyente`
- `POST /api/consultar-vehiculo`
- `POST /api/consultar-licencia`
- `GET /api/health`

---

**¡Disfruta usando la aplicación!** 🚀

Si tienes problemas, revisa la sección de **Solución de Problemas** arriba.

