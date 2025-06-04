# Fase 1: Build del progetto con Gradle
FROM gradle:8-jdk21-alpine AS builder

WORKDIR /app

COPY . .

RUN gradle fatJar --no-daemon

# Fase 2: Deploy
# Usa un'immagine più leggera con solo JRE per il runtime
FROM openjdk:25-slim

# Crea una directory per l'app
WORKDIR /app

# Copia il fatJar dall'immagine di build
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# Espone la porta su cui gira SparkJava
EXPOSE 8080

# Comando di avvio
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
