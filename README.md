# TalentLink ✨

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-brightgreen.svg)
![MariaDB](https://img.shields.io/badge/Database-MariaDB-blue.svg)
![Maven](https://img.shields.io/badge/Build-Maven-orange.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)

A backend service for an internal collaboration platform with intelligent matching capabilities, built to streamline the recruitment process for vendor-supplied candidates.

---

### **Table of Contents**
1. [📖 About The Project](#-about-the-project)
2. [🎯 Core Workflows](#-core-workflows)
3. [🛠️ Technology Stack](#️-technology-stack)
4. [🚀 Getting Started](#-getting-started)
5. [🧪 Testing the System](#-testing-the-system)
6. [🔌 API Endpoints Summary](#-api-endpoints-summary)
7. [📄 License](#-license)

## 📖 About The Project

TalentLink aims to solve the chaos and inefficiency of managing vendor-supplied candidates through disparate tools like email and instant messaging. By providing a unified platform, Hiring Managers (**HM**) and Interviewers can track candidate statuses, manage interviews, and submit feedback in one place, creating a clear, single source of truth.

This repository contains the **backend API service** for the platform, providing the core functionalities for the MVP 1.5 release.

## 🎯 Core Workflows

#### **Hiring Manager (HM) Workflow**
1.  **Create Accounts**: HMs have the authority to create new interviewer accounts, which are assigned a temporary password.
2.  **Define Demands**: Create new hiring demands (requisitions) and tag them with required specialties (e.g., `JAVA`, `QA`).
3.  **Manage Demands**: Update the status of a demand (`ON_HOLD`, `CLOSED`, `HIRED`) as the hiring process evolves.
4.  **Onboard Candidates**: Add detailed candidate profiles from vendors to their corresponding demands.
5.  **Monitor Progress**: View all interview feedback and the complete history for a candidate, and manually update the candidate's final status (e.g., move a `FINALIST` to `HIRED`).
6.  **Generate Reports**: Generate a formatted email summary of all feedback for a candidate with a single API call, ready to be communicated to vendors.

#### **Interviewer Workflow**
1.  **First-Time Login**: New users must change their temporary password immediately after their first login before they can access the system.
2.  **Discover Opportunities**: On the "Relevant Demands" page, the system automatically displays a list of all open demands that match the interviewer's pre-assigned specialties.
3.  **View Details**: Interviewers can click on any relevant demand to see its details, including the list of candidates.
4.  **Submit Feedback (Self-Service)**: After interviewing any candidate from a matched demand, an interviewer can **proactively** find the candidate and submit their feedback (`PASS`/`FAIL`), without waiting for an assignment from an HM.
5.  **Automated Progression**: The system uses the submitted feedback to automatically advance the candidate to the next round or update their status to `FINALIST` or `REJECTED`.

## 🛠️ Technology Stack

* **Backend**:
    * Java 17
    * Spring Boot 3
    * Spring Security (with JWT)
    * Spring Data JPA (Hibernate)
* **Database**:
    * MariaDB
    * Flyway (Database Migration & Version Control)
* **Build & Dependencies**:
    * Maven
    * Lombok
* **Technical Features**:
    * Global Exception Handling (`@RestControllerAdvice`)
    * Pagination on all list-based APIs (`Pageable`)

## 🚀 Getting Started

Follow these steps to set up and run the project locally.

### **Prerequisites**

* **Java Development Kit (JDK)** - version 17 or later
* **Maven** - version 3.8 or later
* **MariaDB** - version 10.6 or later
* An API client like **Insomnia** or **Postman**

### **Installation & Setup**

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/your-username/talentlink-backend.git](https://github.com/your-username/talentlink-backend.git)
    cd talentlink-backend
    ```

2.  **Configure the database**
    * Log in to your MariaDB instance.
    * Create a new database.
        ```sql
        CREATE DATABASE talentlink_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
        ```

3.  **Configure the application**
    * Open the `src/main/resources/application.properties` file.
    * Update the database connection details to match your local environment:
        ```properties
        spring.datasource.url=jdbc:mariadb://localhost:3306/talentlink_db
        spring.datasource.username=your_db_user
        spring.datasource.password=your_db_password
        ```
    * **(Important)** Change the JWT secret for security:
        ```properties
        # Generate your own strong, Base64-encoded key
        jwt.secret=your-super-strong-base64-encoded-secret-key
        ```

4.  **Run the application**
    * This project uses **Flyway**. On the first launch, it will automatically create all necessary tables, insert seed data (including test users and specialties), and apply all schema updates.
    * Run the following command in the project root directory:
        ```bash
        mvn spring-boot:run
        ```
    * The backend service will start on `http://localhost:8080`.

## 🧪 Testing the System

After starting the application, you can test the core user onboarding flow:

1.  **Log in as HM**: Use the seeded HM account (`hiring.manager@talentlink.com`, password `password`) to call `POST /api/auth/login` and get a token.
2.  **Create New Interviewer**: Use the HM's token to call `POST /api/admin/users` and create a new interviewer account.
3.  **New User First Login**: Use the new interviewer's email and the **temporary password (`Welcome123!`)** to log in and get a token.
4.  **Attempt to Access API**: Use the new user's token to access an endpoint like `GET /api/vendors`. You should receive a `403 Forbidden` error telling you to change your password.
5.  **Forced Password Change**: Use the new user's token to call `PUT /api/users/me/password` and set a new, permanent password.
6.  **Access Granted**: Try accessing `GET /api/vendors` again. The request should now succeed.

## 🔌 API Endpoints Summary

| Method | Path | Description | Required Role(s) |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/login` | Log in a user (email + password). | Public |
| `POST` | `/admin/users` | HM creates a new interviewer account. | `HM` |
| `PUT` | `/users/me/password` | The current user changes their password. | Authenticated |
| `GET` | `/users` | Get a list of users by role. | `HM` |
| `GET` | `/vendors` | Get a list of all vendors. | Authenticated |
| `POST` | `/demands` | Create a new hiring demand. | `HM` |
| `GET` | `/demands` | Get a list of all demands created by the HM. | `HM` |
| `GET` | `/demands/{id}` | Get details for a single demand. | `HM` / Matched `INTERVIEWER` |
| `PUT` | `/demands/{id}/status`| HM updates the status of a demand. | `HM` |
| `GET` | `/demands/relevant` | Interviewer gets a list of matched demands. | `INTERVIEWER` |
| `POST` | `/demands/{id}/candidates`| Add a candidate to a demand. | `HM` |
| `GET` | `/candidates/{id}` | Get details for a single candidate. | `HM` |
| `POST` | `/candidates/{id}/feedback`| Interviewer submits feedback for a candidate. | `INTERVIEWER` |
| `PUT` | `/candidates/{id}/status`| HM manually updates a candidate's status. | `HM` |
| `GET` | `/candidates/{id}/history`| Get the event history for a candidate. | Authenticated |
| `GET` | `/candidates/{id}/vendor-email`| HM generates a formatted feedback summary email. | `HM` |
| `GET` | `/interviews/my-assignments`| Get active interview assignments for a user. | `HM` / `INTERVIEWER`|

## 📄 License

This project is licensed under the MIT License.