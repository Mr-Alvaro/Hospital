FROM eclipse-temurin:17-jre-alpine

COPY target/ms-seguridad-1.0.0-SNAPSHOT.jar java-app.jar

RUN sed -i '/^jdk.tls.disabledAlgorithms/s/TLSv1,//' $JAVA_HOME/conf/security/java.security

ENTRYPOINT ["java", "-jar", "java-app.jar"]
