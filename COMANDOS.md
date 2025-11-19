# 📋 COMANDOS PARA COPIAR Y PEGAR

## ⚡ Ejecución Rápida

### Terminal 1 - Backend:
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4 && mvn spring-boot:run
```

### Terminal 2 - Frontend:
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4/frontend && npm run dev
```

---

## 🔧 Primera Vez - Setup

### Instalar Dependencias Frontend:
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4/frontend && npm install
```

### Compilar Backend:
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4 && mvn clean compile
```

---

## ✅ Verificación

### Verificar Backend:
```bash
curl http://localhost:8080/api/health
```

### Verificar Frontend:
```
Abrir navegador en: http://localhost:5173
```

---

## 🐛 Solución de Problemas

### Matar proceso en puerto 8080:
```bash
lsof -i :8080 && kill -9 $(lsof -t -i :8080)
```

### Matar proceso en puerto 5173:
```bash
lsof -i :5173 && kill -9 $(lsof -t -i :5173)
```

### Limpiar y reinstalar frontend:
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4/frontend && rm -rf node_modules package-lock.json && npm install
```

### Limpiar y recompilar backend:
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4 && mvn clean compile
```

---

## 🛑 Detener

En cada terminal:
```
Ctrl + C
```

---

## 📊 Todo en Uno (Avanzado)

### Iniciar todo con un solo comando:
```bash
cd /Users/mateocisneros/IdeaProjects/ArquitecturaC4 && \
(mvn spring-boot:run &) && \
cd frontend && npm run dev
```

**Nota:** Esto inicia ambos servidores, pero es más difícil de controlar.

---

## 🎯 URLs Importantes

```
Frontend:     http://localhost:5173
Backend API:  http://localhost:8080/api
Health:       http://localhost:8080/api/health
```

---

## 📝 Endpoints API

```bash
# Verificar contribuyente
curl -X POST http://localhost:8080/api/verificar-contribuyente \
  -H "Content-Type: application/json" \
  -d '{"correo":"test@example.com","ruc":"1234567890001"}'

# Consultar vehículo
curl -X POST http://localhost:8080/api/consultar-vehiculo \
  -H "Content-Type: application/json" \
  -d '{"placa":"ABC-1234"}'

# Consultar licencia
curl -X POST http://localhost:8080/api/consultar-licencia \
  -H "Content-Type: application/json" \
  -d '{"cedula":"1234567890","placa":"ABC-1234"}'
```

