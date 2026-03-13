# 8.2 🌱 Spring Profiles (dev / prod)

---

## 1. What is Spring Profile?

Spring Profile is a Spring feature used to run the **same application** in **different environments** like:

- Development (`dev`)
- Production (`prod`)
- Testing (`test`)

Each environment has different configurations, mainly:

- Database
- Credentials
- URLs
- Logging level

Instead of changing code again and again, **Spring Profiles help us switch environments safely**.

---

## 2. Why Do We Need Spring Profiles?

In real projects:

- Prod database → AWS RDS  
- Dev secrets → Local machine  
- Prod secrets → Environment variables  
- Dev database → Local / test DB  

### Without profiles ❌
- Everything in one file → mistakes happen  
- Secrets may get pushed to GitHub  
- Production DB may break during testing  

### With profiles ✅
- Clean separation of environments  
- Safe deployments  
- Industry-standard practice  

---

## 3. Core Idea (Very Important)

👉 **One profile = one environment**  
👉 **Only ONE profile is active at a time**

Spring decides two things based on profile:

1. Which property file to load  
2. Which beans to create  

Using one switch 👇

properties
spring.profiles.active=dev
or
spring.profiles.active=prod

4.Property Files Structure

Typical setup:
```
spring.profiles.active=dev
application.properties
application-dev.properties
application-prod.properties
```

What Spring does:
File	                          Purpose
application.properties	      Common settings
application-dev.properties	  Dev DB + configs
application-prod.properties	  Prod DB + configs

## 4.How Spring Decides Which File to Use

Let’s say this is set:
spring.profiles.active=dev

Step-by-step:

1.Spring starts application
2.Reads spring.profiles.active
3.Finds active profile = dev
4.Loads files in this order:
  - application.properties
  - application-dev.properties
5.Ignores application-prod.properties
👉 Only dev properties are now available in memory

## 5.Using @Profile with Beans

Now look at this code 👇
```
@Profile("dev")
@Bean
public DataSource devDataSource() {
    return new HikariDataSource();
}

@Profile("prod")
@Bean
public DataSource prodDataSource() {
    return new HikariDataSource();
}
```
What Spring checks internally:

- Active profile = dev
- @Profile("dev") → ✅ create bean
- @Profile("prod") → ❌ skip bean

👉 Only ONE DataSource bean exists at runtime

❓ Big Doubt: Both Methods Return Same Code, How DB Changes?

Very important concept 👇
Profile does NOT change the code
Profile changes the environment

Key Truth
return new HikariDataSource();

This line:

- DOES NOT choose database
- DOES NOT choose properties
- ONLY creates DataSource object
The database details are already decided earlier.

## 6.How Does DataSource Get DB Details?

Spring Boot uses auto-binding.

When DataSource bean is created, Spring automatically injects values from:

- spring.datasource.url
- spring.datasource.username
- spring.datasource.password

From already loaded profile file.

Example:

application-dev.properties

spring.datasource.url=jdbc:mysql://localhost:3306/devdb
spring.datasource.username=root
spring.datasource.password=root


application-prod.properties

spring.datasource.url=jdbc:mysql://rds.amazonaws.com:3306/proddb
spring.datasource.username=admin
spring.datasource.password=${DB_PASSWORD}


👉 Same code
👉 Different DB
👉 Controlled only by profile

🧠 Correct Execution Order (Must Remember)
```
spring.profiles.active
        ↓
Loads application-<profile>.properties
        ↓
@Profile beans are filtered
        ↓
DataSource auto-binds properties
```

This is the exact internal flow.

## ⚠️ Common Mistakes Developers Make
- Thinking DataSource method chooses DB ❌
- Mixing dev & prod properties ❌
- Forgetting spring.profiles.active ❌
- Hardcoding passwords ❌
- Pushing secrets to GitHub ❌

## ✅ Best Practice (Industry Standard)

- Use properties-only profiles for most cases
- Use @Profile beans only when behavior differs
- Use environment variables in prod
- Never hardcode secrets

🛠️ Fixed Physical Steps (How to Do It Practically)
Step 1️⃣Create property files
application.properties
application-dev.properties
application-prod.properties

Step 2️⃣ Add DB configs

application-dev.properties

spring.datasource.url=jdbc:mysql://localhost:3306/devdb
spring.datasource.username=root
spring.datasource.password=root

application-prod.properties

spring.datasource.url=jdbc:mysql://rds.amazonaws.com:3306/proddb
spring.datasource.username=admin
spring.datasource.password=${DB_PASSWORD}

Step 3️⃣ Activate profile

Local run:
spring.profiles.active=dev

AWS / CI-CD:
SPRING_PROFILES_ACTIVE=prod

Step 4️⃣ (Optional) Use Profile-based DataSource
```
@Configuration
public class DataSourceConfig {

    @Profile("dev")
    @Bean
    public DataSource devDataSource() {
        return new HikariDataSource();
    }

    @Profile("prod")
    @Bean
    public DataSource prodDataSource() {
        return new HikariDataSource();
    }
}
```
🎯 Final Takeaway

Profile decides properties
Properties decide database
Code remains same

This is how real production systems handle dev → prod switching safely.



