# Secret Agency Message Service

## Overview

The Secret Agency Message Service is a secure messaging application designed to handle secret messages that self-destruct after being retrieved. This service, built using Spring Boot, communicates with a NATS messaging server and uses MySQL for data persistence. The application also includes features like automatic message deletion after a specified period and secure encryption for message storage.

## Features

- **Secure Message Storage**: Messages are encrypted and stored in a MySQL database. Messages self-destruct after being retrieved or after a certain period.
- **NATS Integration**: The service integrates with a NATS messaging server for handling message-related operations.
- **Automatic Cleanup**: A scheduled task deletes messages older than two days by default.
- **Customizable Settings**: Key parameters like the number of decryption attempts, auto-delete period, and database credentials are configurable via environment variables.
- **GraalVM**: The service is optimized for deployment using GraalVM to reduce memory footprint and startup time.

## Project Structure

```
.
├── src/main/java/com/tartayadir/cryptoservice
│   ├── configuration         # Contains configuration classes, e.g., NatsConfig.java
│   ├── domain                # Contains entity classes like Message.java and CoreEntity.java
│   ├── exception             # Custom exceptions used in the service
│   ├── mapper                # Utility classes for mapping messages, e.g., MessageMapper.java
│   ├── repository            # JPA repositories for data access
│   ├── service               # Interfaces and implementations of service classes
│   └── util                  # Utility classes like SecureRandomString.java
├── src/main/resources
│   ├── application.yml       # Spring Boot application configuration
│   └── db/changelog          # Liquibase changelogs for database schema management
├── Dockerfile                # Dockerfile for building the application image
├── docker-compose.yml        # Docker Compose file for running the application stack
└── README.md                 # This file
```

## Prerequisites

Before starting the application, ensure you have the following installed:

- Docker
- Docker Compose
- Java 17+
- GraalVM (optional but recommended)
- A terminal or command line interface

## Environment Variables

The application relies on several environment variables for configuration. Set these variables in your shell or in a `.env` file at the root of the project.

### Required Environment Variables
All that are provided below can be use for tests 

```
# MySQL Database Configuration
export MYSQL_DATABASE=mydatabase
export MYSQL_USER=myuser
export MYSQL_PASSWORD=secret
export MYSQL_ROOT_PASSWORD=verysecret

# Spring DataSource Configuration
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mydatabase
export SPRING_DATASOURCE_USERNAME=myuser
export SPRING_DATASOURCE_PASSWORD=secret

# NATS Server Configuration
export NATS_URL=nats://localhost:4222
export NATS_SERVER_NAME=nats-server
export NATS_USER=your-user
export NATS_PASSWORD=your-password
```

Alternatively, you can create a `.env` file with the following content:

```
# .env file content

# MySQL Database Configuration
MYSQL_DATABASE=mydatabase
MYSQL_USER=myuser
MYSQL_PASSWORD=secret
MYSQL_ROOT_PASSWORD=verysecret

# Spring DataSource Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mydatabase
SPRING_DATASOURCE_USERNAME=myuser
SPRING_DATASOURCE_PASSWORD=secret

# NATS Server Configuration
NATS_URL=nats://localhost:4222
NATS_SERVER_NAME=nats-server
NATS_USER=your-user
NATS_PASSWORD=your-password
```

## Building and Running the Application

### Step 1: Clone the Repository

Clone the repository to your local machine:

```
git clone https://github.com/your-repo/secret-agency-message-service.git
cd secret-agency-message-service
```

### Step 2: Set Up Environment Variables

Set up the environment variables as described above. This can be done by exporting them in your terminal or by creating a `.env` file.

### Step 3: Build the Docker Image

Build the Docker image using the provided Dockerfile:

```
docker-compose up --build
```

This command will:

- Build the Docker image for the Secret Agency Message Service using GraalVM.
- Start the MySQL and NATS containers.
- Start the Secret Agency Message Service container.

### Step 4: Accessing the Application

Once the containers are running, the application will be accessible at `http://localhost:8080`.

### Step 5: Verify the Setup

You can verify the setup by checking the logs of the running containers:

```
docker-compose logs
```

Ensure that the services are starting without errors and that the application can connect to both MySQL and NATS.

## Scheduled Tasks

The application includes a scheduled task that automatically deletes messages older than two days. This task is configured using a cron expression and runs daily at midnight. The cutoff time and other parameters can be customized using environment variables.

## Customization

### Adjusting Configuration Parameters

You can adjust key configuration parameters by modifying the environment variables:

- **Decryption Attempts**: Adjust the maximum number of decryption attempts by changing the code or adding a configurable parameter.
- **Message Auto-Deletion Period**: Change the cutoff time for message deletion by modifying the scheduled task logic.

### GraalVM Customization

If using GraalVM, you can further customize the application by adjusting the GraalVM settings in the Dockerfile. This includes enabling native-image builds if required.

## Contributing

Contributions are welcome! Please follow the standard GitHub workflow:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/your-feature`).
3. Commit your changes (`git commit -am 'Add some feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Create a pull request.

## Troubleshooting

- **Database Connection Issues**: Ensure that MySQL is running and that the environment variables are correctly set.
- **NATS Connection Issues**: Verify that the NATS server is up and running and that the correct URL is being used.
- **Docker Build Issues**: Ensure that Docker and Docker Compose are installed and up to date. Check for any errors in the Dockerfile.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Thanks to the open-source community for providing tools and libraries used in this project.
- Special thanks to [Your Organization/Name] for providing the opportunity to work on this project.

```

This README provides a comprehensive overview of the Secret Agency Message Service, including setup instructions, environment configurations, and customization options. Follow these steps to get the application up and running in your environment.
