# Use a imagem oficial do OpenJDK 21 como base
FROM maven:3.9.2-eclipse-temurin-17-alpine as builder

# Defina o diretório de trabalho dentro do contêiner
COPY ./src src/
COPY ./pom.xml pom.xml

RUN mvn clean package -DskipTests

# Copie o arquivo JAR da sua aplicação para dentro do contêiner
FROM eclipse-temurin:17-jre-alpine
COPY target/download-upload-files-0.0.1-SNAPSHOT.jar my-app.jar

# Comando para executar a aplicação quando o contêiner for iniciado
CMD ["java", "-jar", "my-app.jar"]


