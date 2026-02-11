# TalentAI - AI-Augmented Recruitment Platform 🚀

**TalentAI** is an innovative software solution designed to transform recruitment processes. By combining the power of **Spring Boot**, **React**, and Artificial Intelligence, the platform enables intelligent matching between candidates and job offers, assessment management, and recruitment workflow tracking.

## 🌟 Key Features

* **Intelligent Matching (AI)**: Utilization of an AI service to evaluate the fit between candidate profiles and job requirements.
* **Dedicated Dashboards**:
* **HR Dashboard**: Overview of applications, job offer management, and a Kanban system for process tracking.
* **Candidate Dashboard**: Profile management, application tracking, and access to job postings.


* **Real-Time Notifications**: Integrated alert system for both recruiters and candidates.
* **Advanced Security**: Secure authentication and authorization for different user roles.

## 🛠️ Technical Architecture

The project is built on a modern and scalable architecture:

* **Backend**: Java 17+, Spring Boot 3, Spring Data JPA.
* **Frontend**: React.js with a responsive user interface.
* **Database**: PostgreSQL (configured via Docker and Spring).
* **DevOps & CI/CD**: Jenkins, Docker, Kubernetes (K8s).
* **Monitoring**: Integrated Prometheus and Grafana for performance tracking.

## Quick Start Guide

### 1. Prerequisites

* Docker & Docker Compose
* Java 17 and Maven
* Node.js & npm

### 2. Launching with Docker (Full)

The simplest method to launch the entire ecosystem (App, DB, Monitoring):

```bash
# Launch main services (App + DB)
docker-compose up -d

# Launch operations services (Jenkins, Prometheus, Grafana)
docker-compose -f ops-compose.yml up -d

```

### 3. Manual Installation

**Backend (Spring Boot):**

```bash
# In the root folder
./mvnw clean install
./mvnw spring-boot:run

```

*The backend will be accessible at `http://localhost:8080`.*

**Frontend (React):**

```bash
cd frontend
npm install
npm start

```

*The frontend will be accessible at `http://localhost:3000`.*

## ☸️ Kubernetes Deployment

The project is cloud-ready with complete Kubernetes configurations:

* **App Deployment**: `kubectl apply -f k8s/app-deployment.yaml`.
* **Monitoring**: Files in `k8s/monitoring/` allow for deploying Prometheus and Grafana on your cluster.
* **GitOps CI/CD**: Planned integration with **ArgoCD** via `k8s/argocd-app.yaml`.

## 📂 Repository Structure

* `/src`: Java source code for the backend.
* `/frontend`: React application.
* `/k8s`: Kubernetes manifests for deployment and monitoring.
* `Jenkinsfile`: Automated CI/CD pipeline.

---
