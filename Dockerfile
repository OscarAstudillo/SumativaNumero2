# Etapa 1: Construcción (Multi-stage)
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compilar el .JAR ignorando los tests para acelerar el proceso
RUN mvn clean package -DskipTests

# Etapa 2: Ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Configurar un usuario seguro sin privilegios (no-root)
USER nobody

# Copiar solo el JAR resultante de la etapa de construcción
COPY --from=build /app/target/techstore-api-0.0.1-SNAPSHOT.jar app.jar

# Documentar el puerto que expone la aplicación
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]