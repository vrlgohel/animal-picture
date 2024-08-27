# Use the official OpenJDK image to run the application
FROM openjdk:17-jdk

# Set the working directory
WORKDIR /app

# Copy the jar file to the working directory
COPY /target/animal-picture-0.0.1-SNAPSHOT.jar ./animal-picture-0.0.1-SNAPSHOT.jar

# Expose the port that the application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "animal-picture-0.0.1-SNAPSHOT.jar"]
