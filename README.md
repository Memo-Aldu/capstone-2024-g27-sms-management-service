# Message / SMS Management service

## Overview
The **SMS Management Service** is a modular Spring Boot application designed to manage SMS/MMS communications efficiently. Built with **Spring Modulith**, the service streamlines message handling, conversation tracking, and provider integrations. It serves as a core component for **DPR Group**, enabling scalable, provider-agnostic communication capabilities for talent acquisition and engagement.
![image](https://github.com/user-attachments/assets/9b30d076-ddfe-4cc1-bfbb-6d6bc1733785)

---
## Notes:
Full documentation found [here](https://github.com/Memo-Aldu/capstone-2024-g27-wiki/wiki/SMS-Management-System) 

## Features
- **Advanced Messaging Operations**:
  - Send, schedule, and cancel SMS/MMS.
  - Support for bulk messaging operations.
- **Conversation Management**:
  - Logical grouping of messages into conversations.
  - API for creating, retrieving, and updating conversations.
- **Provider Agnostic Design**:
  - Abstraction layer for SMS/MMS providers.
  - Initial support for Twilio with flexibility for future integrations.
- **Scalability and Reliability**:
  - Modular architecture enables independent scaling of services.
  - Comprehensive test coverage with 96% instruction and 79% branch coverage.
- **Postman Collection**:
  - Pre-configured API requests for testing and integration.

---

## Architecture
The application is structured into four core modules:
1. **Core Module**:
   - Shared domain models, database configuration, and event listeners.
2. **Message Module**:
   - Handles SMS/MMS operations, including sending, scheduling, and cancellation.
3. **Conversation Module**:
   - Manages conversations, linking messages to threads and tracking their status.
4. **Provider Module**:
   - Abstracts external communication with SMS/MMS providers (e.g., Twilio).

Each module is designed for loose coupling and high cohesion, ensuring maintainability and flexibility.

---

## Requirements
- **Java 21**: Required for running the application.
- **Maven**: For dependency management.
- **MongoDB**: NoSQL database for message and conversation persistence.
- **Docker**: To run MongoDB and the application in containers.
- **Twilio Account**: For sending SMS/MMS.
- **Ngrok**: For exposing local development servers.

---

## Installation

1. **Clone the Repository**:
   ```bash
   git clone git@github.com:Memo-Aldu/capstone-2024-g27-sms-management-service.git
   cd capstone-2024-g27-sms-management-service
   ```

2. **Set Up Environment Variables**:
   - Copy the example file:
     ```bash
     cp .env.example .env
     ```
   - Update `.env` with:
     - MongoDB configuration
     - Twilio credentials
     - Ngrok callback host

3. **Run MongoDB**:
   ```bash
   docker-compose up -d mongodb
   ```

4. **Start the Application**:
   ```bash
   ./start-dev.sh
   ```

---

## Testing

### Unit and Integration Tests
Run all tests:
```bash
mvn test
```
- **Coverage**:
  - 96% instruction coverage.
  - 79% branch coverage.

---

## Usage

### API Endpoints
The service exposes RESTful endpoints for managing messages and conversations. Refer to the included **Postman Collection** for detailed usage examples.

#### Example Endpoints
- **Send SMS**:
  ```bash
  POST /api/v1/messages
  ```
- **Create Conversation**:
  ```bash
  POST /api/v1/conversation
  ```

### Authentication
- OAuth2 Bearer Token is required for all endpoints.
- Use the `auth` endpoint in the Postman Collection to generate tokens.

---

## Configuration

### Twilio Setup
- Add Twilio credentials (`ACCOUNT_SID`, `AUTH_TOKEN`, etc.) to the `.env` file.
- Configure `SCHEDULING_SMS_SID` and `BULK_SMS_SID` for messaging services.

### MongoDB Setup
- MongoDB runs in a Docker container.
- Environment variables in `.env` configure MongoDB connection.

---

## Contact
For questions or support, please contact **Memo Aldujaili** at [maldu064@uottawa.ca](mailto:maldu064@uottawa.ca).
