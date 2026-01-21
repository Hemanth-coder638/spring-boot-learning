# 8.3 ☁️ Elastic Beanstalk – Physical Implementation Roadmap (AWS Free Tier)

This section explains **HOW to do things practically**, in the **correct real-world order**.  
Think of this as a **production deployment checklist**.

---

## 🧭 Big Picture Roadmap (Understand First)
Before going step by step, understand the overall flow:

Local Spring Boot App
   ↓
GitHub Repository
   ↓
Elastic Beanstalk Environment
   ↓
EC2 (App runs here)
   ↓
RDS (Database)


Everything must align in this order.  
Now let’s break it down step by step.

---

## 🪜 STEP 1: Prepare Spring Boot Application (Local)

Before touching AWS, your application **must be production-ready**.

You must ensure:

- Application runs successfully on local machine
- Database credentials are **NOT hardcoded**
- Secrets are read from **environment variables**
- Spring Profiles (`dev` / `prod`) are properly configured

### Mandatory checks

- `mvn clean package` works
- JAR file is generated
- Application starts without errors

⚠️ **Common mistake**  
Deploying an app that fails locally → Elastic Beanstalk deployment fails immediately.

---

## 🪜 STEP 2: Create AWS RDS (Database First)

Always create **RDS before Elastic Beanstalk**.

### AWS Console Steps

1. Go to **RDS**
2. Click **Create database**
3. Choose:
   - PostgreSQL / MySQL
   - Free Tier
4. Set:
   - Database name
   - Username
   - Password

### Important settings (for learning)

- Public access: **Yes**
- VPC: **Default**
- Security Group: Allow database port

⚠️ **Common mistakes**

- Forgetting DB port (5432 / 3306)
- Wrong username or password
- DB not publicly accessible during learning phase

---

## 🪜 STEP 3: Configure RDS Security Group (CRITICAL)

This is the **most important step**.

### What you must do

- Edit RDS security group
- Allow inbound traffic:
  - Port: DB port (5432 / 3306)
  - Source: **Elastic Beanstalk EC2 security group**

### Why this matters

EC2 must be allowed to talk to RDS.

⚠️ If this is wrong →  
Application starts → **Database connection fails**

---

## 🪜 STEP 4: Prepare `application-prod.yml`

This file is **only for production**.

### Example

```yaml
spring:
  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
```
❌ Never put real credentials here
✅ Only environment variable placeholders

## 🪜 STEP 5: Create Elastic Beanstalk Application
Now move to Elastic Beanstalk.
AWS Console steps:
1.Go to Elastic Beanstalk
2.Create application
3.Select:
  - Platform: Java
  - Version: Corretto / Java 17
4.Upload:
  - JAR or ZIP
This creates:
- EC2
- Load Balancer
- Security Groups
- IAM Role
All automatically.

## 🪜 STEP 6: Configure Environment Variables (MOST IMPORTANT)
In Elastic Beanstalk → Configuration → Environment properties
Add:
- SPRING_PROFILES_ACTIVE=prod
- DB_URL
- DB_USERNAME
- DB_PASSWORD
- OAuth keys (if any)

Why?
This is how EB injects values into your app.
⚠️ Common mistakes:
Typo in variable name
Forgetting SPRING_PROFILES_ACTIVE
Using wrong DB URL

🪜 STEP 7: Understand EC2 Created by EB
Elastic Beanstalk automatically:
- Creates EC2
- Installs Java
- Runs your JAR
You do not SSH normally.

If needed:
- Use EC2 logs
- Use EB logs

🪜 STEP 8: Understand Security Groups (EB + RDS)
EB creates:
- One security group for EC2
- One for Load Balancer
- You must ensure:
- EC2 SG → allowed to access RDS SG
- HTTP/HTTPS open
⚠️ Most DB connection issues are security group issues.

## 🪜 STEP 9: Logs & Debugging
If app fails:
- Go to EB → Logs → Request logs
- Check:
  - App crash
  - DB connection error
  - Profile issues
Logs come from CloudWatch.

## 🪜 STEP 10: Verify Application
Once deployment is successful:
- EB gives URL
-Open in browser
 Test APIs using Postman
If API works → Deployment success 🎉

⚠️ Real Challenges Developers Face (Important)
Based on real experience:
- DB connection timeout
- Wrong environment variable names
- Secrets accidentally pushed to GitHub
- Wrong Java version
- App runs locally but fails in EB
- RDS security group misconfiguration

These are normal problems, not mistakes.

## 🧠 Key Rule to Remember
Elastic Beanstalk fails silently if configuration is wrong.
Always check logs.

## 🎓 Final One-Line Summary
Elastic Beanstalk deployment is successful only when application configuration, environment variables, security groups, and database connectivity are aligned correctly.

