# ⚡ GUÍA RÁPIDA DE EJECUCIÓN

## 🚀 Ejecutar en 4 Pasos

### 1️⃣ Abrir Terminal y Navegar
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4
```

### 2️⃣ Iniciar Backend (Terminal 1)
```bash
mvn spring-boot:run
```
✅ Espera hasta ver: `Started ArquitecturaC4Application`  
🌐 Backend corriendo en: http://localhost:8080

### 3️⃣ Abrir Nueva Terminal e Iniciar Frontend (Terminal 2)
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4/frontend
npm run dev
```
✅ Espera hasta ver: `Local: http://localhost:5173/`  
🌐 Frontend corriendo en: http://localhost:5173

### 4️⃣ Abrir Navegador
```
http://localhost:5173
```

---

## 🛑 Detener

**En cada terminal:**
```
Ctrl + C
```

---

## ⚠️ Primera Vez - Instalar Dependencias

### Backend (solo si falla):
```bash
mvn clean compile
```

### Frontend (solo primera vez):
```bash
cd frontend
npm install
```

---

## 🔍 Verificar que Funciona

```bash
# Verificar backend
curl http://localhost:8080/api/health

# Debe responder:
# {"status":"UP",...}
```

---

## 📋 Requisitos

- ✅ Java 17+
- ✅ Maven 3.6+
- ✅ Node.js 16+
- ✅ Internet (para Redis Cloud)

---

## 🐛 Problemas Comunes

### Puerto 8080 ocupado:
```bash
lsof -i :8080
kill -9 <PID>
```

### Puerto 5173 ocupado:
```bash
lsof -i :5173
kill -9 <PID>
```

### npm install falla:
```bash
npm cache clean --force
npm install
```

---

## 📊 Resumen Visual

```
┌─────────────────────────────────────┐
│ Terminal 1                          │
│ mvn spring-boot:run                 │
│ ↓                                   │
│ Backend: http://localhost:8080      │
│           ↓                         │
│           ↓ Redis Cloud ☁️          │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Terminal 2                          │
│ npm run dev                         │
│ ↓                                   │
│ Frontend: http://localhost:5173     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ Navegador                           │
│ http://localhost:5173               │
│ ↓                                   │
│ 🇪🇨 Consultas SRI y ANT             │
└─────────────────────────────────────┘
```

---

## 📚 Más Información

Ver **GUIA_EJECUCION.md** para guía completa paso a paso.

