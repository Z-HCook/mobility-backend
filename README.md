Wasel Palestine — Smart Mobility & Checkpoint Intelligence Platform
1. System Overview
Wasel Palestine is an API-centric smart mobility platform designed to navigate daily movement challenges by providing reliable mobility intelligence.

2. Technical Stack
Language: Java 21

Framework: Spring Boot 3

Database: PostgreSQL (Supabase)

API Documentation: API-Dog

Performance Testing: k6 by Grafana

Deployment: Docker

3. Architecture & Database
Architecture

+-----------------------------------------------------------+
|                  Wasel Palestine Architecture              |
+-----------------------------------------------------------+
|           |                                               |
+-----------|-----------------------------------------------+
|  [Spring Boot 3 / Java 21]                                |
|  +-----------------------------------------------------+  |
|  |  Controller Layer (API Endpoints)                   |  |
|  +-----------------------------------------------------+  |
|  |  Use-Case / Service Layer (Business Logic)          |  |
|  +-----------------------------------------------------+  |
|  |  Repository Layer (Spring Data JPA)                 |  |
|  +-----------------------------------------------------+  |
|  |  [Security: JWT Filter]    [Config: Cache/External] |  |
|  +-----------------------------------------------------+  |
+-----------|-----------------------------------------------+
            |
            v (JDBC / SQL)
+-----------------------------------------------------------+
|  [Supabase / PostgreSQL Database (Cloud)]                 |
+-----------------------------------------------------------+

Database Schema (ERD)
<img width="1434" height="765" alt="ERD" src="https://github.com/user-attachments/assets/f29457c6-d552-45f6-a29b-3882c23337da" />

4. API Design
All endpoints are versioned (/api/v1/...). Security is implemented using JWT (Access + Refresh tokens).

[Link to API-Dog Documentation]

5. Performance Load Testing (k6)
We evaluated the system under 5 scenarios:

Read-Heavy: Incident listing optimization (p95 < 5ms).

Write-Heavy: Subscription service stability.

Mixed Workload: Throughput & cache hit rate analysis.

Spike Testing: Stress testing at 200 VUs.

Soak Testing: Sustained load resilience.

Refer to the full Performance_Report.pdf in the repository.

6. Development Workflow
Git Strategy: Feature branching with mandatory Pull Requests.

CI/CD: Dockerized environment for seamless deployment.
