FROM openjdk:17-alpine
WORKDIR /opt
COPY target/*.jar /opt/demo-book-list.jar
ENTRYPOINT exec java $JAVA_OPTS -jar demo-book-list.jar
