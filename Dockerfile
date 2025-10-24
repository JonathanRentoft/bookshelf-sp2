# Use OpenJDK 17 as base image
FROM eclipse-temurin:17-jre-alpine

# Set working directory
WORKDIR /app

# Copy the packaged jar file
COPY target/app.jar /app/app.jar

# Expose the application port
EXPOSE 7070

# Set environment variables for database connection (can be overridden)
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=bookshelf
ENV DB_USER=postgres
ENV DB_PASSWORD=postgres

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
