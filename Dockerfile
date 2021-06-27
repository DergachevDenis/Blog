FROM tomcat:9.0.48-jdk8-openjdk-buster

RUN apt update && apt install maven -y

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY ROOT.xml .

RUN cp /app/src/main/resources/blog_docker.properties /app/src/main/resources/blog.properties

RUN mvn package

RUN mkdir -p /usr/local/tomcat/conf/Catalina/localhost/
RUN cp /app/ROOT.xml /usr/local/tomcat/conf/Catalina/localhost/

#RUN cp /app/target/My_Blog.war /usr/local/tomcat/webapps/
