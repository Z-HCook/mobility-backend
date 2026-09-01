# 🚗 Wasel Palestine — Smart Mobility & Checkpoint Intelligence Platform

An API-centric backend platform designed to provide reliable mobility intelligence for navigating daily movement challenges in Palestine.

---

## 📌 Overview

Wasel Palestine is a backend-focused platform that provides structured mobility information through versioned RESTful APIs (`/api/v1/...`).

The system is designed to serve mobile applications, web dashboards, and other clients while focusing on real-world backend engineering concerns such as security, scalability, performance, caching, and maintainability.

---

## ✨ Key Features

* 🚧 **Incident & Checkpoint Management** — Manage mobility incidents and checkpoint status updates.
* 🔔 **Subscriptions** — Subscribe to checkpoint updates and get real-time mobility alerts.
* 🔐 **JWT Authentication** — Secure access using Access & Refresh Tokens.
* ⚡ **Caching** — Enhance API latency and reduce database hits using Caffeine.
* 🚦 **Rate Limiting** — Protect endpoints from excessive traffic using Bucket4j.

---

## 🛠️ Tech Stack

| Technology                | Purpose                                   |
| :------------------------ | :---------------------------------------- |
| **Java 21**               | Primary programming language              |
| **Spring Boot 3**         | Application framework & REST APIs         |
| **PostgreSQL / Supabase** | Relational cloud database                 |
| **Spring Data JPA**       | ORM & data persistence layer              |
| **Spring Security + JWT** | Role-based authorization & authentication |
| **Caffeine**              | In-memory application caching             |
| **Bucket4j**              | API rate limiting & throttling            |
| **Docker**                | Containerized deployment                  |
| **k6**                    | Performance & load testing                |
| **Apidog**                | API documentation & testing               |

---

## 🏗️ Architecture

The application follows a clean layered architecture:

```text
Client Applications (Mobile / Web)
  │
  ▼
REST API (/api/v1)
  │
  ▼
Spring Security + JWT Filter
  │
  ▼
Controller Layer (API Endpoints)
  │
  ▼
Service Layer (Business Logic + Caching / Rate Limiting)
  │
  ▼
Repository Layer (Spring Data JPA)
  │
  ▼
PostgreSQL Database (Supabase Cloud)
```

---

## 🗄️ Database

Wasel Palestine relies on PostgreSQL, hosted on Supabase, with Spring Data JPA managing relational queries.

### 📐 Entity Relationship Diagram (ERD)

<img width="1434" height="765" alt="Wasel Palestine ERD" src="https://github.com/user-attachments/assets/f29457c6-d552-45f6-a29b-3882c23337da" />

---

## 🔐 API Design & Documentation

All endpoints are versioned using:

```text
/api/v1/...
```

Security is implemented using **JWT (Access + Refresh Tokens)**.

📖 **[View API Documentation](https://ee1ys8ldya.apidog.io)**

---

## 📊 Performance Testing

The backend was evaluated using **k6 by Grafana** under five workload scenarios:

* **Read-heavy workloads** — Incident listing optimization.
* **Write-heavy workloads** — Subscription service stability.
* **Mixed workloads** — Throughput and cache hit rate analysis.
* **Spike testing** — Stress testing at up to **200 Virtual Users (VUs)**.
* **Soak testing** — Sustained-load resilience.

One of the read-heavy tests achieved **p95 latency below 5 ms**.

---

## 🚀 Getting Started

### Prerequisites

* Java 21
* Maven
* Active PostgreSQL / Supabase connection credentials
* Docker

### Clone the Repository

```bash
git clone https://github.com/Z-HCook/mobility-backend.git
cd mobility-backend
```

### Run the Application

**Linux / macOS:**

```bash
./mvnw spring-boot:run
```

**Windows:**

```bash
mvnw.cmd spring-boot:run
```

> **Note:** Set up the required database and JWT environment variables in your local application configuration before launching the application.

---

## 🌿 Development Workflow

**Git Strategy:** Feature branches with Pull Request reviews.

**Commit Conventions:** Descriptive and standardized commit messages.

**Deployment:** Dockerized environment for consistent deployment.

---

## 👥 Team

**Advanced Software Engineering — Spring 2026**
**An-Najah National University**

**Supervisor:** Dr. Amjad AbuHassan

### Team Members

* **Khadejah Al-Etyani**
* **Dana Ismail**
* **Zeina Hanani**
* **Masa Kanaze'**

---

## 🔗 Repository

**[Wasel Palestine — Backend GitHub Repository](https://github.com/Z-HCook/mobility-backend)**
