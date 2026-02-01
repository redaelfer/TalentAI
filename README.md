# TalentAI - Plateforme de Recrutement Augmentée par l'IA 

**TalentAI** est une solution logicielle innovante conçue pour transformer les processus de recrutement. En combinant la puissance de **Spring Boot**, **React** et de l'intelligence artificielle, la plateforme permet de matcher intelligemment les candidats avec les offres d'emploi, de gérer les évaluations et de suivre le workflow de recrutement.

## 🌟 Fonctionnalités Clés

* **Matching Intelligent (IA)** : Utilisation d'un service d'IA pour évaluer la correspondance entre les profils des candidats et les exigences des offres.
* **Tableaux de Bord Dédiés** :
* **Dashboard RH** : Vue d'ensemble des candidatures, gestion des offres et système Kanban pour le suivi des processus.
* **Dashboard Candidat** : Gestion du profil, suivi des candidatures et accès aux offres.


* **Notifications en Temps Réel** : Système d'alertes intégré pour les recruteurs et les candidats.
* **Sécurité Avancée** : Authentification et autorisation sécurisées pour les différents rôles d'utilisateurs.

## 🛠️ Architecture Technique

Le projet repose sur une architecture moderne et scalable :

* **Backend** : Java 17+, Spring Boot 3, Spring Data JPA.
* **Frontend** : React.js avec une interface utilisateur réactive.
* **Base de Données** : PostgreSQL (configuré via Docker et Spring).
* **DevOps & CI/CD** : Jenkins, Docker, Kubernetes (K8s).
* **Monitoring** : Prometheus et Grafana intégrés pour le suivi des performances.

## Guide de Démarrage Rapide

### 1. Prérequis

* Docker & Docker Compose
* Java 17 et Maven
* Node.js & npm

### 2. Lancement avec Docker (Complet)

La méthode la plus simple pour lancer tout l'écosystème (App, DB, Monitoring) :

```bash
# Lancement des services principaux (App + DB)
docker-compose up -d

# Lancement des services d'exploitation (Jenkins, Prometheus, Grafana)
docker-compose -f ops-compose.yml up -d

```

### 3. Installation Manuelle

**Backend (Spring Boot) :**

```bash
# Dans le dossier racine
./mvnw clean install
./mvnw spring-boot:run

```

*Le backend sera accessible sur `http://localhost:8080`.*

**Frontend (React) :**

```bash
cd frontend
npm install
npm start

```

*Le frontend sera accessible sur `http://localhost:3000`.*

## ☸️ Déploiement Kubernetes

Le projet est prêt pour le cloud avec des configurations Kubernetes complètes :

* **Déploiement de l'application** : `kubectl apply -f k8s/app-deployment.yaml`.
* **Monitoring** : Les fichiers dans `k8s/monitoring/` permettent de déployer Prometheus et Grafana sur votre cluster.
* **CI/CD GitOps** : Intégration prévue avec **ArgoCD** via `k8s/argocd-app.yaml`.

## 📂 Structure du Repository

* `/src` : Code source Java du backend.
* `/frontend` : Application React.
* `/k8s` : Manifestes Kubernetes pour le déploiement et le monitoring.
* `Jenkinsfile` : Pipeline CI/CD automatisé.
