FROM maven:3.3.9


COPY . /PerformanceManagementSystem
WORKDIR /PerformanceManagementSystem
RUN file="$(ls -1)" && echo $file
RUN mvn clean install -DskipTests

#RUN mvn test
WORKDIR /PerformanceManagementSystem/api-performance-mgmt/target/
RUN file="$(ls -1)" && echo $file
Expose 8081

ENTRYPOINT ["java","-jar","api-performance-mgmt-0.jar"]