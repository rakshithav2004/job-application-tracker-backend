# Job Application Tracker - Spring Boot Backend

A comprehensive REST API for tracking job applications, built with Spring Boot, MongoDB, JWT authentication, and comprehensive security features. Perfect for managing your job search process with real-time status tracking and analytics.

## 🚀 Features

- **User Authentication & Authorization**
  - User registration and login with JWT token-based authentication
  - Role-based access control (USER, ADMIN)
  - Secure password encryption using BCrypt

- **Job Application Management**
  - Create and track job applications
  - Update application status (Applied, Interview, Offer, Rejected)
  - Search jobs by company name
  - Get comprehensive statistics on applications

- **Admin Dashboard**
  - View all users and their job applications
  - System-wide statistics

- **API Documentation**
  - Swagger UI integration for interactive API testing
  - OpenAPI 3.0 specifications
  - Bearer token authentication configured

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.5.14
- **Database**: MongoDB
- **Authentication**: JWT (JSON Web Token)
- **Security**: Spring Security with BCrypt
- **API Documentation**: Springdoc OpenAPI (Swagger 3)
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

- Java 17 or higher
- MongoDB running locally (default: `localhost:27017`) or update connection string in configuration
- Maven 3.6+ (or use provided `mvnw`)

## ⚙️ Setup Instructions

### 1. Clone and Navigate to Project
```bash
cd job-application-tracker
```

### 2. Configure Environment (Optional but Recommended)
For production, set environment variables:
```bash
# Linux/Mac
export JWT_SECRET="your-very-secure-random-secret-key-min-32-chars"

# Windows PowerShell
$env:JWT_SECRET="your-very-secure-random-secret-key-min-32-chars"
```

### 3. Build the Project
```bash
./mvnw clean install
```

### 4. Run the Application
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## 📚 API Endpoints

### Authentication
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and get JWT token

### Job Applications
- `POST /jobs` - Create new job application
- `GET /jobs/user/{userId}` - Get all applications for a user
- `PUT /jobs/{id}/status` - Update application status
- `DELETE /jobs/{id}` - Delete an application
- `GET /jobs/search?company={name}` - Search jobs by company
- `GET /jobs/stats/{userId}` - Get statistics for user

### User
- `GET /users/me` - Get current user profile (requires authentication)

### Admin
- `GET /admin/users` - Get all users (admin only)
- `GET /admin/jobs` - Get all job applications (admin only)
- `GET /admin/stats` - Get system statistics (admin only)

## 🔐 Authentication

### Using JWT Token

1. **Register New User**
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "securePassword123"
  }'
```

2. **Login**
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "securePassword123"
  }'
```

3. **Use Token in Requests**
```bash
curl -X GET http://localhost:8080/users/me \
  -H "Authorization: Bearer {token}"
```

## 📊 Example Usage

### Create Job Application
```bash
curl -X POST http://localhost:8080/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {token}" \
  -d '{
    "companyName": "TechCorp",
    "role": "Senior Java Developer"
  }' \
  -G --data-urlencode "userId=user123"
```

### Update Application Status
```bash
curl -X PUT http://localhost:8080/jobs/{jobId}/status \
  -H "Authorization: Bearer {token}" \
  -G --data-urlencode "status=INTERVIEW"
```

### Get User Statistics
```bash
curl -X GET http://localhost:8080/jobs/stats/user123 \
  -H "Authorization: Bearer {token}"
```

## 🧪 Testing

Run all tests:
```bash
./mvnw test
```

Run specific test:
```bash
./mvnw test -Dtest=JobApplicationTrackerApplicationTests
```

## 📖 API Documentation

Once the application is running, access Swagger UI:
```
http://localhost:8080/swagger-ui.html
```

## 🏗️ Project Structure

```
src/main/java/com/jobtracker/
├── config/           # Spring Configuration (Security, Swagger)
├── controller/       # REST Controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA/MongoDB Entities
├── exception/       # Custom Exception Classes
├── repository/      # MongoDB Repositories
├── security/        # JWT and Security Utilities
└── service/         # Business Logic
```

## 🔒 Security Considerations

- Passwords are encrypted with BCrypt
- JWT tokens expire after 24 hours (configurable)
- CSRF protection is disabled for stateless JWT authentication
- Role-based access control on admin endpoints
- Input validation on DTOs
- Error handling with custom exception classes

## ⚠️ Important Notes for Production

1. **Change JWT Secret**: Replace the default JWT secret with a strong, random value (minimum 32 characters)
2. **Use Environment Variables**: Don't commit sensitive data; use environment variables
3. **MongoDB Security**: Configure MongoDB authentication and use secure connection strings
4. **HTTPS**: Always use HTTPS in production
5. **Disable Debug Logging**: Set logging levels appropriately for production
6. **Cross-Origin Configuration**: Configure CORS as needed for your frontend

## 🤝 Contributing

This is a personal resume project. Feel free to fork and extend!

## 📝 License

This project is open source and available under the MIT License.

## 👤 Author

Created as a portfolio project to demonstrate:
- REST API design principles
- Spring Boot best practices
- JWT authentication implementation
- MongoDB integration
- Exception handling and validation
- API documentation with Swagger

