# CollabSpace

A production-grade team collaboration platform built on Java Spring Boot microservices. CollabSpace lets teams create workspaces, manage projects with Kanban boards, collaborate in real-time, and subscribe to premium plans via Razorpay.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-green)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.0-blue)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![License](https://img.shields.io/badge/license-MIT-green)

---

## Architecture

CollabSpace follows a microservices architecture with 7 independent services:

```
Client Request
      ↓
API Gateway (8080)         ← JWT validation, routing
      ↓
Eureka Server (8761)       ← Service discovery
      ↓
┌─────────────────────────────────────────┐
│  Auth Service     (8081)                │
│  User Service     (8082)                │
│  Project Service  (8083)                │
│  Billing Service  (8084)                │
│  Notification Service (8085)            │
└─────────────────────────────────────────┘
```

JWT validation happens only at the Gateway layer. Downstream services receive user identity via `X-Auth-Email` and `X-Auth-Role` headers — a pattern recommended for production microservices.

---

## Services

### API Gateway — Port 8080
Single entry point for all client requests. Validates JWT tokens and forwards user claims as headers to downstream services. All external traffic passes through here.

### Eureka Server — Port 8761
Service registry and discovery. Every service registers here on startup so services can find each other by name without hardcoded URLs.

### Auth Service — Port 8081
Handles user registration and login. Issues JWT access tokens signed with a shared secret. Passwords are hashed with BCrypt.

### User Service — Port 8082
Manages user profiles. Supports avatar upload via Cloudinary. Users can update their name and bio. Includes user search by email for workspace invites.

### Project Service — Port 8083
The core service. Manages workspaces, projects, Kanban boards, and tasks. Features include:
- Workspace creation and member invites
- Project boards with customisable columns
- Task management with assignees, due dates, and priorities
- File attachments via Cloudinary
- Task activity log
- Real-time board updates via WebSocket and STOMP

### Billing Service — Port 8084
Handles workspace subscription plans. Free plan supports up to 3 members. Pro plan (₹999/month) is unlimited. Integrates with Razorpay for payment processing and handles payment webhooks for automatic plan upgrades.

### Notification Service — Port 8085
Sends email notifications via Gmail SMTP and real-time in-app notifications via WebSocket. Notification types include task assignments, workspace invites, and payment confirmations. Supports unread count and mark-as-read.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.0 |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | PostgreSQL (one DB per service) |
| ORM | Spring Data JPA + Hibernate |
| File Storage | Cloudinary |
| Payments | Razorpay |
| Real-time | Spring WebSocket + STOMP |
| Email | JavaMailSender + Gmail SMTP |
| Build Tool | Maven |
| Environment | spring-dotenv |

---

## Getting Started

### Prerequisites

- Java 17
- Maven 3.8+
- PostgreSQL 15
- Git

### 1. Clone the Repository

```bash
git clone https://github.com/Yashchatse/CollabSpace.git
cd CollabSpace
```

### 2. Create Databases

Open pgAdmin or psql and run:

```sql
CREATE DATABASE collabspace_auth;
CREATE DATABASE collabspace_user;
CREATE DATABASE collabspace_project;
CREATE DATABASE collabspace_billing;
CREATE DATABASE collabspace_notification;
```

### 3. Configure Environment Variables

Each service reads from its own `.env` file. Copy the example file and fill in your values:

```bash
cp collabspace-auth/.env.example collabspace-auth/.env
cp collabspace-user/.env.example collabspace-user/.env
cp collabspace-project/.env.example collabspace-project/.env
cp collabspace-billing/.env.example collabspace-billing/.env
cp collabspace-notification/.env.example collabspace-notification/.env
```

**Auth Service `.env`:**
```
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_min_32_chars
```

**User Service `.env`:**
```
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_min_32_chars
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

**Project Service `.env`:**
```
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_min_32_chars
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

**Billing Service `.env`:**
```
DB_USERNAME=postgres
DB_PASSWORD=your_password
RAZORPAY_KEY_ID=rzp_test_your_key_id
RAZORPAY_KEY_SECRET=your_key_secret
```

**Notification Service `.env`:**
```
DB_USERNAME=postgres
DB_PASSWORD=your_password
MAIL_USERNAME=yourgmail@gmail.com
MAIL_PASSWORD=your_16_char_app_password
```

### 4. Start Services

Start in this exact order — Eureka must be first:

```bash
# Terminal 1
cd collabspace-eureka && mvn spring-boot:run

# Terminal 2 (after Eureka is up)
cd collabspace-gateway && mvn spring-boot:run

# Terminal 3
cd collabspace-auth && mvn spring-boot:run

# Terminal 4
cd collabspace-user && mvn spring-boot:run

# Terminal 5
cd collabspace-project && mvn spring-boot:run

# Terminal 6
cd collabspace-billing && mvn spring-boot:run

# Terminal 7
cd collabspace-notification && mvn spring-boot:run
```

### 5. Verify All Services

Open `http://localhost:8761` — you should see all 6 services registered in the Eureka dashboard.

---

## API Reference

All requests go through the Gateway at `http://localhost:8080`. Protected endpoints require a Bearer token in the Authorization header.

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Register new user |
| POST | `/api/auth/login` | No | Login and get JWT token |

### Users

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/users/me` | Yes | Get my profile |
| PUT | `/api/users/me` | Yes | Update profile |
| POST | `/api/users/me/avatar` | Yes | Upload avatar |
| GET | `/api/users/search?email=` | Yes | Search users |

### Workspaces

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/workspaces` | Yes | Create workspace |
| GET | `/api/workspaces` | Yes | Get my workspaces |
| GET | `/api/workspaces/{id}` | Yes | Get workspace |
| POST | `/api/workspaces/{id}/invite?email=` | Yes | Invite member |
| POST | `/api/workspaces/{id}/projects` | Yes | Create project |

### Projects & Tasks

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/projects/{id}` | Yes | Get project |
| GET | `/api/projects/workspace/{id}` | Yes | Get projects in workspace |
| POST | `/api/tasks/column/{columnId}` | Yes | Create task |
| PATCH | `/api/tasks/{id}` | Yes | Update task |
| PATCH | `/api/tasks/{id}/move` | Yes | Move task to column |
| GET | `/api/tasks/column/{columnId}` | Yes | Get tasks in column |
| POST | `/api/tasks/{id}/attachments` | Yes | Upload attachment |
| GET | `/api/tasks/{id}/activity` | Yes | Get task activity log |

### Billing

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/billing/order` | Yes | Create Razorpay order |
| POST | `/api/billing/webhook` | No | Razorpay webhook |
| GET | `/api/billing/plan/{workspaceId}` | Yes | Get workspace plan |

### Notifications

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/notifications/send` | Yes | Send notification |
| GET | `/api/notifications` | Yes | Get my notifications |
| GET | `/api/notifications/unread-count` | Yes | Get unread count |
| PATCH | `/api/notifications/mark-read` | Yes | Mark all as read |

---

## WebSocket

Connect to real-time updates using STOMP over SockJS.

**Board updates** (Project Service):
```
Endpoint: ws://localhost:8080/ws
Subscribe: /topic/board/{projectId}
```

**Notification bell** (Notification Service):
```
Endpoint: ws://localhost:8080/ws-notification
Subscribe: /topic/notifications/{userEmail}
```

---

## External Services Setup

### Cloudinary (File Uploads)
1. Create a free account at [cloudinary.com](https://cloudinary.com)
2. Go to Dashboard and copy your Cloud Name, API Key, and API Secret

### Razorpay (Payments)
1. Create an account at [razorpay.com](https://razorpay.com)
2. Go to Settings → API Keys → Generate Test Key
3. Copy your Key ID and Key Secret

### Gmail (Email Notifications)
1. Enable 2-Step Verification on your Google account
2. Go to Security → App Passwords → Generate
3. Use the 16-character password as `MAIL_PASSWORD`

---

## Security

- JWT tokens are validated only at the Gateway layer
- Downstream services receive user identity via trusted internal headers
- All credentials are managed via `.env` files — never committed to Git
- Passwords are hashed with BCrypt
- Each service has its own isolated PostgreSQL database

---

## Project Structure

```
collabspace-parent/
├── collabspace-eureka/          Service discovery
├── collabspace-gateway/         API Gateway + JWT validation
├── collabspace-auth/            Authentication + JWT issuance
├── collabspace-user/            User profiles + Cloudinary
├── collabspace-project/         Workspaces + Kanban + WebSocket
├── collabspace-billing/         Razorpay payment integration
├── collabspace-notification/    Email + real-time notifications
├── .env.example                 Environment variable template
├── .gitignore
└── pom.xml                      Parent POM
```

---

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Commit your changes: `git commit -m "feat: add your feature"`
4. Push to your branch: `git push origin feature/your-feature`
5. Open a Pull Request to the `dev` branch

---

## License

This project is licensed under the MIT License.

---

## Author

**Yash Chatse**
- GitHub: [@Yashchatse](https://github.com/Yashchatse)

---

> Built as a portfolio project to demonstrate production-grade Java microservices architecture.