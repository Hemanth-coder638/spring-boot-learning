# Week 08 – Spring Boot Deployment with CI/CD (AWS)

## Overview
This week focuses on deploying a production-ready Spring Boot application on AWS using CI/CD best practices.

Covered topics:
- Production database using AWS RDS (PostgreSQL)
- Spring Profiles (dev / prod)
- Elastic Beanstalk architecture
- CI/CD using AWS CodePipeline & CodeBuild
- Database migration using Flyway

Detailed explanations are available in the folders below.

## Documentation Index
- 8.1 Production Database with RDS
- 8.2 Spring Profiles (dev / prod)
- 8.3 Elastic Beanstalk Architecture
- 8.4 CI/CD with AWS CodePipeline
- 8.5 Database Migration using Flyway

## Reference Project Used for This Module

To practically apply the concepts covered in Week 08, I deployed the following Spring Boot application on AWS:

🔗 **Spring Employee Service (Production Deployment)**
https://github.com/Hemanth-coder638/spring-employee-service

### What this project demonstrates:
- CI pipeline triggered from GitHub
- Maven build executed using AWS CodeBuild
- Deployment to AWS Elastic Beanstalk
- PostgreSQL database hosted on AWS RDS
- Environment-based configuration using Spring Profiles
- Flyway used for database versioning and migrations

## AWS Deployment Summary

This application was successfully deployed on AWS using the following setup:

- **Compute**: AWS Elastic Beanstalk
- **Instance Type**: EC2 (managed by EB)
- **Database**: AWS RDS (PostgreSQL)
- **CI/CD**: AWS CodePipeline + CodeBuild
- **Source Control**: GitHub
- **Migration Tool**: Flyway

The deployment follows a real-world production flow where:
- Every GitHub push triggers a CI pipeline
- CodeBuild runs Maven build and tests
- Elastic Beanstalk deploys the packaged application
- Application connects securely to RDS
