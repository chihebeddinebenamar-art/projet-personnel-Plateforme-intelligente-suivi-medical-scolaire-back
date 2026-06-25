# Plateforme intelligente de suivi médical scolaire

Plateforme web dédiée au suivi de la santé des élèves dans un établissement scolaire. Elle centralise le carnet médical numérique, les vaccinations, les consultations, les accidents et les échanges avec les parents, avec des recommandations de prévention assistées par IA.

## Objectif

Faciliter le travail des infirmiers scolaires et des administrateurs tout en donnant aux parents un accès sécurisé aux informations médicales de leurs enfants.

## Fonctionnalités principales

- **Carnet médical numérique** : allergies, maladies, photos du carnet, fiche médicale
- **Vaccinations** : suivi, types de vaccins, rappels par classe
- **Consultations et accidents** : enregistrement et historique
- **Espace parent** : consultation des élèves, notifications
- **Espace infirmier** : gestion médicale des élèves
- **Administration** : gestion des élèves, classes, niveaux et carnets
- **Recommandations IA** : conseils de prévention et plan de vaccination personnalisé

## Architecture

| Module | Rôle | Technologies |
|--------|------|--------------|
| `pfe-plateforme` | Frontend / couche présentation | Spring Boot, JSP, Spring Security |
| `project-back-ecode-nc-main` | Backend / API métier | Spring Boot, JPA, MySQL, SOAP |

Communication entre le frontend et le backend via **services web SOAP**.

## Rôles utilisateurs

- **Administrateur** : gestion des élèves, classes et carnets médicaux
- **Infirmier** : suivi médical, vaccinations, consultations
- **Parent** : consultation des informations de ses enfants et notifications

## Technologies

- Java 21
- Spring Boot
- Spring Security
- JPA / Hibernate
- MySQL
- SOAP / JAXB
- JSP / JSTL

## Prérequis

- JDK 21
- Maven 3.6+
- MySQL 8+
- Serveur d’application (Tomcat)

## Installation

1. Cloner le dépôt
2. Configurer la base MySQL dans le backend
3. Lancer le backend : `mvn spring-boot:run` (module `PFEBackEnd/PFE`)
4. Lancer le frontend : `mvn spring-boot:run` (module `pfe-plateforme`)

## Auteur

Projet personnel — Projet de Fin d’Études (PFE)
