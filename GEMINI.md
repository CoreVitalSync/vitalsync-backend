# Project Context: VitalSync - Medication & Patient Monitoring System

> **AI INSTRUCTIONS:**
> You are a Senior Backend Developer utilizing Java and Quarkus.
> Currently, we are building the **Backend and Web MVP** for this project (Mobile is a future phase).
> All code suggestions must align with the Functional Scope defined below.
> **Guiding Principles:**
> 1. **Privacy First:** Prioritize data privacy (LGPD/GDPR) and encryption for sensitive data.
> 2. **Architecture:** Use a Modular Monolith approach with strict separation of concerns (DTOs, Mappers, Services, Repositories).
> 3. **Clean Code:** Avoid "magic strings", use Enums, and enforce input validation (@Valid).
> 4. **Stack Consistency:** Stick to Quarkus Panache, Lombok, and Hibernate Validator as defined in the stack.

---

## 1. Technical Stack

* **Backend Framework:** Quarkus (Supersonic Subatomic Java)
* **Language:** Java 17+
* **Database:** PostgreSQL (Dockerized for Dev, Cloud for Prod)
* **ORM:** Hibernate ORM with Panache
* **Security:** SmallRye JWT (RBAC: Patient vs Doctor), BCrypt
* **Architecture:** Modular Monolith (Packages: user, medication, vitals, sharing)
* **Frontend (MVP):** Web Interface (Technology TBD)

---

## 2. Functional Scope

### 1. Project Objective

* Develop a system (starting with Web MVP) to assist polymedicated patients or those with comorbidities (diabetes, hypertension, hypothyroidism, etc.) in controlling their medications and vital signs, enabling reminder notifications, easy recording, and secure data sharing with doctors.

* In addition to supporting treatment adherence, the app will be a strategic differentiator for the agency, adding value to medical marketing services and creating a user base for future integrations and partnerships with clinics and pharmacies.

### 2. User Profiles

**Patient**
* Receives medication reminders/notifications.
* Easily records whether or not they took the medication.
* Records vital signs (blood pressure, blood glucose, heart rate, etc.).
* Views history (charts, evolution).
* Decides whether to share data with the doctor.

**Doctor**
* Receives authorized access to patient data (treatment adherence, evolution of vital signs).
* Can view daily, weekly, and monthly reports.
* In the future, will be able to issue digital prescriptions with electronic signatures and manage prescriptions via the app.

**System Administrator (Optional in MVP)**
* Manages registrations, access, and permissions.
* Maintains control of integrations and general reports.

### 3. Core Features (MVP)

**User Registration and Login**
* Simple registration (name, email, age).
* Separate login for patient and doctor (Role-Based Access).

**Medication Management**
* Registration of medications (name, dose, schedules).
* Quick checklist to confirm intake.
* Notifications (Email/Web Push) at the scheduled time.

**Vital Signs Recording**
* Fields for blood pressure, blood glucose, heart rate.
* Basic history in list format.
* Quick view of daily status (e.g., "Pressure controlled / off target").

**Sharing with Doctors**
* Patient chooses whether or not to share data.
* Doctor views vital signs and medication adherence.

**Smart Notifications**
* Notifications to remind the patient.
* Future possibility to integrate alerts for guardians/caregivers.

### 4. Advanced Features (For Future Evolution)

**Integration with Wearables**
* Smartwatches that measure pressure, heart rate, sleep.

**AI Agent on WhatsApp**
* Conversational assistant for quick recording ("I took medication X") and consultation of side effects.
* Chatbot to answer basic questions ("Could this symptom be a side effect?").

**Digital Prescriptions with Electronic Signature**
* Doctor issues digital prescription directly in the app.
* Integration with digital signature APIs (e.g., Clicksign, Certisign).

**Partnerships with Pharmacies**
* Integration with pharmacy chains (Droga Raia, Drogasil).
* Direct purchase and delivery of medications.
* Automatic replenishment notifications.

**Reports and Insights with AI**
* Automatic analysis of vital sign evolution (daily, weekly, monthly).
* Habit suggestions and predictive alerts ("trend of increasing pressure").

### 5. Project Assumptions

* The system must be prepared for both Web and Mobile clients.
* The **MVP** will focus on the **patient (Web)**, with basic functionalities to record medications and vital signs.
* Sharing with doctors will be via patient-controlled access (e.g., invitation link/token).
* Integrations with AI, hardware, and pharmacies will be considered **future phases**.
* Sensitive data must be protected with **LGPD (GDPR equivalent) and encryption**.

### 6. Out of Scope (in MVP)

* Integration with smartwatches and monitoring devices.
* Issuance of digital prescriptions with electronic signature.
* Integration with pharmacies and monetization via partnerships.
* Advanced analytical dashboards for doctors.
* Predictive AI with automatic reports.

### Summary of Patient Flow in MVP

1.  **Registration/Login** in the web app.
2.  Adds medications (name, dose, schedules).
3.  Receives notifications to remember.
4.  Confirms whether they took it or not.
5.  Records vital signs manually.
6.  Decides whether to share data with the doctor.