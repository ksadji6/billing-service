# Étape 1 : Build avec Maven et Java 21
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app

# Installation de Maven
RUN apk add --no-cache maven

# Optimisation du cache pour les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Build du JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution légère
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copie du JAR (billing-service-*.jar)
COPY --from=build /app/target/billing-service-*.jar app.jar

# Le Billing-Service tourne sur le port 8083
EXPOSE 8083

# Limitation de la RAM pour éviter de saturer ton PC pendant la démo
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]