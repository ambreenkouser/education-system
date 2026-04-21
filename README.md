📘 EduManage Pro – School Management System

EduManage Pro is a comprehensive, scalable, and secure School Management System designed to digitize and streamline academic and administrative operations for educational institutions.

This project is built using a modern microservices architecture with a Java backend and a React frontend, ensuring high performance, flexibility, and scalability.

🚀 Key Features
👨‍💼 Admin Module
User management (Teachers, Students, Parents)
Fee structure configuration & voucher generation
Fee payment approval/rejection workflow
Reports (attendance, fee status, academic performance)
👨‍🏫 Teacher Module
Mark and manage attendance
Create exams and quizzes from a question bank
Upload assignments and grade submissions
Analyze student performance
🎓 Student Module
View attendance and academic records
Attempt exams and submit assignments
Track grades and feedback
Personalized dashboard
👨‍👩‍👧 Parent Module
Monitor child’s academic progress
View attendance and grades
Upload fee payment slips
Receive notifications
🏗️ Architecture

This system follows a Microservices Architecture for better scalability and maintainability.

🔹 Backend (Java Microservices)
Java 17
Spring Boot
Spring Cloud (API Gateway, Config)
Spring Security (JWT Authentication)
PostgreSQL (Database per service)
Redis (Caching & sessions)
Kafka (Event-driven communication)
🔹 Frontend
React.js + TypeScript
Redux Toolkit (State management)
RTK Query (API handling)
🔹 Infrastructure
Docker & Docker Compose
Nginx (optional)
AWS S3 / MinIO (file storage)
🔐 Security Features
JWT-based authentication (Access + Refresh tokens)
Role-Based Access Control (RBAC)
Secure file upload with restricted access
Encrypted data transmission (HTTPS)
Input validation and sanitization
📦 Core Modules
Authentication & Authorization
User Management
Attendance Management
Examination & Question Bank
Assignment Management
Fee Management System
Notification System
🔄 System Workflow Highlights
Teachers manage attendance, exams, and grading
Students participate in exams and track performance
Parents monitor progress and manage fee payments
Admin controls system operations and approvals
⚙️ Setup & Installation (High-Level)
# Clone repository
git clone <repo-url>

# Start services using Docker
docker-compose up --build

# Access application
Frontend: http://localhost:3000
Backend Gateway: http://localhost:8080
🧪 Testing
Unit Testing: JUnit, Mockito
Integration Testing: Spring Boot Test
API Testing: Postman / Supertest
E2E Testing: Playwright
📈 Future Enhancements
Mobile application (Android/iOS)
AI-based performance analytics
Online classes integration
Advanced reporting dashboards
🤝 Contribution

Contributions are welcome! Please follow coding standards and submit pull requests for review.

📄 License

This project is licensed under the MIT License.
