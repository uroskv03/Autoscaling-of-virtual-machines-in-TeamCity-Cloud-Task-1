# Autoscaling-of-virtual-machines-in-TeamCity-Cloud-Task-1

## Remote Command Executor Service

A backend service that allows users to execute shell commands on remote executors. 	

Each command is executed inside an isolated Docker container with configurable CPU limits. The system tracks the execution lifecycle and allows users to get the status of their tasks.

## Client 

Client using Postman can: 

*    send a command with CPU count 

*    check execution status (QUEUED, IN_PROGRESS, FINISHED, FAILED)

## Service

For every incoming command, the service dynamically pulls the necessary image (Alpine Linux) and initializes a new Docker container.

The container is configured with Docker HostConfig to enforce CPU limits.

The service:


*    executes commands asynchronously using background threads.

*    automatically updates task status in MongoDB (QUEUED -> IN_PROGRESS ->  FINISHED/FAILED).

A StartupRecovery mechanism scans database on application startup and marks any interrupted tasks (e.g. potential crash) as FAILED.

## Tech Stack

*    Java 17+

*    Spring Boot

*    MongoDB

*    Docker (Remote Executor)

*    Docker-Java Library

## Requirements

*    Docker Desktop

*    MongoDB 

*    VS Code Extensions: Extension Pack for Java, Spring Boot Extension Pack, Docker.

## Project Setup

Starting project: Spring Initializr

Project: Maven

Language: Java

Spring Boot: 4.0.3

Packaging: Jar; 

Configuration: Properties

Java: 17

Dependencies: Spring Web, Spring Data MongoDB, Lombok

## How to run

Clean and run the application using Maven: 

```
./mvnw clean install
```
```
./mvnw spring-boot:run
```

Submit a command in Postman: 

```
POST /api/execute with json format 
```

Examples: 
```
{
    "command": "echo 'Hello World'",
    "cpuCount": 1
}

{
    "command": "sha1sum /dev/zero & sha1sum /dev/zero & sha1sum /dev/zero",
    "cpuCount": 3
}

{
    "command": "sleep 60 && echo '60 secs'",
    "cpuCount": 1
}
```


Check status in Postman: 
```
GET /api/status/{id} 
```

## Verifying Resource Limits

To verify that the service is correctly applying resource limits, you can use the following Docker commands while a task is **IN_PROGRESS**:

1. List active containers:

```
docker ps
```

2. CPU usage in real-time:

```
docker stats
```
3. Detailed information about specific command:

```
docker inspect <CONTAINER ID>
```

## Original Task Description

Create a simple service to execute shell commands on a remote executor.

A user should be able to:

* send a command script to be executed
    * specify the necessary resources (e.g. CPU count) for the executor
* get status of an execution (QUEUED/IN PROGRESS/FINISHED)

A service:

* when a user sends a command, starts a new executor
* waits for executor’s initialisation
* execute the command on the executor
* updates the execution status



