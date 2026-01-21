# 8.4 🔁 CI/CD for Spring Boot on AWS (Free Tier)

This document explains how to set up a **simple but real production-style CI/CD pipeline** for a Spring Boot application using **AWS Free Tier**.

We use:
- **GitHub** → Source control
- **AWS CodePipeline** → Orchestration
- **AWS CodeBuild** → Build & Test
- **AWS Elastic Beanstalk** → Deployment

Every time you push code to GitHub, the pipeline will **automatically build and deploy** your application.
---
## 🔄 Pipeline Overview (Big Picture)

### Code Flow

GitHub → CodePipeline → CodeBuild → Elastic Beanstalk. 

### In Simple Words

- **GitHub**: You push code (commit / merge).
- **CodePipeline**: Detects the change and starts the pipeline.
- **CodeBuild**: Compiles, tests, and creates a JAR file.
- **Elastic Beanstalk**: Deploys the new JAR to EC2.

If the build **fails**, deployment **stops**.  
If the build **passes**, deployment **happens automatically**.

This is the core idea of CI/CD.

---

## 📥 Connect GitHub to CodePipeline

### Steps in AWS Console

1. Go to **AWS CodePipeline**
2. Click **Create pipeline**
3. Give a pipeline name
4. Source provider → **GitHub (Version 2)**
5. Click **Connect to GitHub**
6. Authorize AWS in GitHub
7. Select:
   - Repository
   - Branch (main / master)
8. Enable:
   - ✅ *Start the pipeline on source code change*

This creates a **webhook**.

### What This Means

- AWS is allowed to read your GitHub repo
- Every new commit triggers the pipeline automatically

---

## ⚙️ CodeBuild & `buildspec.yml`

### What is CodeBuild?

CodeBuild is the service that:
- Runs Maven
- Compiles code
- Runs tests
- Produces a JAR file

### What is `buildspec.yml`?

`buildspec.yml` is a **YAML file** that tells CodeBuild:
- What commands to run
- How to build the application
- Which files to send to next stage

### Sample `buildspec.yml`

```yaml
version: 0.2

phases:
  install:
    commands:
      - echo "Installing dependencies"
      - mvn install -DskipTests

  build:
    commands:
      - echo "Building Spring Boot JAR"
      - mvn package

artifacts:
  files:
    - target/*.jar
```
## What Happens Internally
- Maven downloads dependencies
- JAR file is created inside target/
- CodeBuild sends JAR to S3 as an artifact
- CodePipeline passes artifact to Elastic Beanstalk
If any command fails, the pipeline stops here.

☁️ Deploy to Elastic Beanstalk
What Elastic Beanstalk Does

Elastic Beanstalk:

- Creates EC2
- Configures Load Balancer
- Manages Auto Scaling
- Deploys your JAR
- You don’t manage servers manually.
# Deploy Stage in CodePipeline
- Provider: AWS Elastic Beanstalk
-Select:
  -Application name
  -Environment name
CodePipeline sends the artifact → EB deploys it.

# Cost Note (Free Tier)

- Elastic Beanstalk itself is free
- You pay only for:
  - EC2
  - S3

- Free Tier gives:
  - 750 hours/month of t2.micro
  - 5 GB S3

## 🔒 IAM Roles & Permissions
## 1️⃣ CodePipeline Service Role

This role allows CodePipeline to:
- Read GitHub source
- Store artifacts in S3
- Trigger CodeBuild
- Deploy to Elastic Beanstalk

Required permissions:
- S3 access
- CodeBuild access
- Elastic Beanstalk access
⚠️ If permissions are missing → deploy stage hangs or fails.

2️⃣ CodeBuild Service Role

This role allows CodeBuild to:
- Read source artifact from S3
- Write build artifact to S3
Usually needs:
- AmazonS3FullAccess (for learning)
- Additional permissions if accessing other AWS services

3️⃣ Elastic Beanstalk EC2 Instance Role
Elastic Beanstalk EC2 uses:
-aws-elasticbeanstalk-ec2-role

Must have:
- AWSElasticBeanstalkWebTier
- AWSElasticBeanstalkWorkerTier

This allows:

- Uploading logs
- Reading config
- Managing app processes

🆓 AWS Free Tier & CodeArtifact Notes
Free Tier Summary
- CodePipeline:
  - 1 free active pipeline
- CodeBuild:
  - 100 free build minutes / month
- Elastic Beanstalk:
  - No extra cost
- EC2 & S3:
  - Covered under Free Tier limits

## ⚠️ Real-World Issue: CodeArtifact “SubscriptionRequiredException”
What Happened
While setting up CI, an error appeared:
```
SubscriptionRequiredException
```
This happens when:
- CodeBuild tries to use AWS CodeArtifact
- But the service is not enabled / subscribed

# Important Truth
👉 CodeArtifact is NOT required for basic Spring Boot CI/CD.

# Why This Error Can Be Ignored
- Maven can download dependencies directly from Maven Central
- JAR artifact is stored in S3
- Elastic Beanstalk deploys directly from S3
 
# Fix
- Remove any CodeArtifact login/config from buildspec.yml
- Use normal mvn package
Pipeline works perfectly without CodeArtifact.

## 👷 Physical Implementation Steps (End-to-End)
# Step 1️⃣ Create Elastic Beanstalk Application
- Go to Elastic Beanstalk
- Create application
- Choose Java platform
- Wait until environment is green

# Step 2️⃣ Prepare GitHub Repository
- Push Spring Boot project
- Ensure:
  - pom.xml
  - buildspec.yml in root

# Step 3️⃣ Create CodeBuild Project
- Select OS + Java + Maven
- Use buildspec.yml from source
- Ensure service role has S3 access

# Step 4️⃣ Create CodePipeline
- Source: GitHub
- Build: CodeBuild
- Deploy: Elastic Beanstalk
- Accept or verify IAM roles

# Step 5️⃣ Trigger Pipeline
- Push a commit to GitHub
- Pipeline starts automatically
- Watch stages in AWS Console

# Step 6️⃣ Handle Failures
- If build fails:
  - Check CodeBuild logs
  - Fix code or buildspec
  - Push again

# Step 7️⃣ Verify Deployment
- Open Elastic Beanstalk URL
- Test APIs using browser or Postman

## 🔁 What Happens on Fail vs Pass
# If Build Fails ❌
- Pipeline stops
- No deployment
- Existing app remains running

#If Build Passes ✅
- New JAR is deployed
- Old version is replaced
- App updates automatically

## 🎯 Final Takeaway

# CI/CD ensures that:
- Code is always buildable
- Broken code never reaches production
- Deployment is fast, repeatable, and safe
This setup mirrors real industry pipelines, even on AWS Free Tier.


🙏 Happy coding! With this setup, your Spring Boot app goes from code to cloud automatically, using AWS Free Tier services. 

