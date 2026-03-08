# Tara - Cab Booking System

![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.6-brightgreen)
![React](https://img.shields.io/badge/React-19.1.0-blue)
![Tailwind CSS](https://img.shields.io/badge/Tailwind_CSS-v4.2.1-38B2AC)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-latest-336791)

**Tara** is a modern, full-stack Cab Booking System designed to seamlessly connect riders with drivers. This project is built using a robust Java Spring Boot backend and a fast, responsive React frontend.

## 🚀 Current Status: Building Authentication

We are currently actively developing the **Authentication module**, integrating JWT-based secure login, user registration, and validation across both the backend and frontend components.

---

## ✨ Features

- **Authentication (Work in Progress):** Secure user registration, robust login logic, and complete session management utilizing JSON Web Tokens (JWT).

---

## 💻 Tech Stack

### ⚡ Frontend
- **Framework:** React 19 (Vite)
- **Styling:** Tailwind CSS v4
- **State Management & Data Fetching:** TanStack Query (React Query)
- **Global State Management:** Zustand
- **HTTP Client:** Axios
- **Routing:** React Router DOM
- **Validation:** Zod (for client-side schema validation)
- **Language:** TypeScript

### 🛠️ Backend
- **Framework:** Spring Boot 3.5.6 (Java 17)
- **Security:** Spring Security & JSON Web Tokens (JJWT)
- **ORM:** Spring Data JPA with Hibernate
- **Database:** PostgreSQL (Production) & H2 (Development)
- **Utilities:** Lombok, MapStruct (DTO-Entity mapping)
- **Build Tool:** Maven

---

## 📁 Project Structure

The project is structured as a monorepo containing the following workspaces:

### 🛠️ Backend (`/cab-booking-backend`)
A modular Spring Boot application structured by functional layers:
- `config/`: Application and security configurations.
- `controller/`: REST API controllers and endpoints.
- `dto/`: Data Transfer Objects for standardized API payloads.
- `exception/`: Global and custom exception handlers.
- `mapper/`: MapStruct interfaces for automatic entity-DTO conversion.
- `model/`: JPA entities representing database tables.
- `repository/`: Spring Data JPA repositories for database access.
- `security/`: JWT filters, authentication providers, and security rules.
- `service/`: Core business logic encapsulation.
- `util/`: Reusable helper and utility classes.

### ⚡ Frontend (`/cab-booking-frontend`)
A feature-driven React application utilizing Vite:
- `api/`: Axios client configuration and network services.
- `components/`: Modular, reusable React components.
- `features/`: Domain-specific modules (e.g., `auth`, encapsulating its own UI, hooks, and logic).
- `hooks/`: Custom React hooks for abstracted component logic.
- `layout/`: Global structure components (e.g., navigation, headers, wrappers).
- `lib/`: Integrations with third-party libraries and schema validation (Zod).
- `providers/`: React context providers (e.g., TanStack Query).
- `routes/`: Application routing configuration and definitions.
- `store/`: Zustand stores for global state management.
- `types/`: Global TypeScript definitions and interfaces.
- `ui/`: Fundamental atomic UI components (base building blocks).

### 📚 Documentation (`/docs`)
- Contains project-level documentation, tutorial references, and learning guides.

---

## ⚙️ Getting Started

Follow these steps to set up the project locally for development and testing.

### Prerequisites
- [Node.js](https://nodejs.org/) (v20+ recommended)
- [Java 17+](https://adoptium.net/)
- [Maven](https://maven.apache.org/)
- [PostgreSQL](https://www.postgresql.org/)

### 1. Setting up the Backend

1. Navigate to the backend directory:
   ```bash
   cd cab-booking-backend
   ```
2. Configure your database:
   Update `src/main/resources/application.properties` (or `.yml`) with your PostgreSQL (or H2) credentials. By default, an in-memory H2 database can be used for rapid development.
3. Build and Run:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   The API will be available at `http://localhost:8080`.

### 2. Setting up the Frontend

1. Navigate to the frontend directory:
   ```bash
   cd cab-booking-frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Environment Setup:
   Ensure you have a `.env` file referencing the backend API endpoints.
4. Run the development server:
   ```bash
   npm run dev
   ```
   The frontend will be available at `http://localhost:5173` (or the port specified by Vite).

---

## 🛡️ Authentication Flow (Work in Progress)

- **Registration:** Secure user creation validated on the client via Zod and handled securely on the backend via Spring Security.
- **Login:** Users authenticate using their credentials. The Spring backend intercepts the request, verifies credentials, and issues an encrypted JWT.
- **Session Management:** The React frontend utilizes Axios interceptors to automatically attach the JWT to all outgoing secured requests, ensuring stateful session tracking powered seamlessly by Zustand and Tanstack Query.

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the issues page.

## 📝 License

This project is open-source and available under the [MIT License](LICENSE).
