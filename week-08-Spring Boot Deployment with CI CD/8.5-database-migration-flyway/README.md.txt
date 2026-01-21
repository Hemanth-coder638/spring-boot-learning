Flyway Migration Guide for Spring Boot (AWS RDS Postgres) 🚀

This README explains database migrations with Flyway in a simple way. We use PostgreSQL on AWS RDS as our database. You will learn why migrations are needed, what Flyway does, and how to set it up in Spring Boot. We keep the language easy and add emojis and examples. 👍

Why Database Migrations? 🤔

When our app changes (new tables, columns, etc.), we need to update the database too. Database migration means managing these schema changes in an organized, version-controlled way[1]. Without it, developers might run SQL by hand and forget or make mistakes. This can cause different environments (dev, test, prod) to get out of sync and lead to bugs or downtime.
Think of migrations like versioning for your database schema – just like how code is versioned in Git. Migrations ensure each change is applied only once and in order[2]. They make deployments safe and repeatable. For example, if you add a new column in your code, a Flyway script will add it to the database, so the app code and DB stay in sync.

What is Flyway? 🛠️

Flyway is a popular open-source tool (by Redgate) for database migrations. It uses simple SQL (or Java) scripts to define changes. Flyway keeps track of what has been applied, so it only runs new changes. In other words, Flyway scans a folder of versioned scripts, sorts them by version number, and executes them one by one[3][2]. It records each successful migration in a special table (flyway_schema_history) in your database[4][3]. This way, Flyway “knows” which migrations ran already and will never run them twice.
Behind the scenes, Flyway does these steps on startup or when you run the CLI: 1. Scan for scripts: It looks in classpath:db/migration (by default) for files named like V1__init.sql, V2__add_column.sql, etc.[5][6]. 2. Check history table: It finds (or creates) the flyway_schema_history table in your database. This table stores every applied version number, description, and checksum[3]. 3. Compare and sort: Flyway ignores scripts already applied, then sorts the remaining scripts by version number. 4. Apply migrations: It executes each pending script in order, updating the database schema. Each migration is run inside a transaction (if supported) so it is rolled back on failure[7]. 5. Update history: After each successful script, Flyway inserts a row into flyway_schema_history with the version, script name, execution time, etc.[3].
This process makes schema changes predictable and repeatable[8]. Flyway will prevent accidental changes (e.g. it will error if you modify an already applied script)[9][2].

Folder Structure & File Naming 📂

Flyway expects your SQL scripts in a specific directory and naming format. By default, it looks in src/main/resources/db/migration on the classpath[5]. Inside this db/migration folder, you place your .sql files. The filenames must follow this pattern for versioned migrations:
V<VERSION>__<DESCRIPTION>.sql
- V is a literal prefix. - <VERSION> is the version number (like 1, 2, 1.1, or 001_002). - Then two underscores __. - Then a short description, e.g. init, create_users_table (use underscores, no spaces). - Example: V1__init.sql, V2__add_email_to_users.sql, V2_1__update_orders.sql[5][6].
Flyway will ignore any files that don’t match this naming convention, so it’s critical to name them exactly (two underscores) or it will throw “Invalid SQL filenames” errors. Each script is run exactly once in order.
You can also use repeatable migrations (prefix R__), which run whenever changed (useful for views or refresh scripts), and undo migrations (prefix U__) in Flyway Teams. But the simplest case is V-files.
Here’s an example project layout:
src/
└─ main/
   ├─ java/                # your Java code
   └─ resources/
       ├─ application.yml  # Spring Boot config
       └─ db/
           └─ migration/
               ├─ V1__init.sql
               ├─ V2__add_user_table.sql
               └─ R__refresh_views.sql
The code above shows where to put migrations. Remember to use classpath:db/migration or set spring.flyway.locations if you use a different folder[5].

Setup Flyway in Spring Boot ⚙️

To enable Flyway auto-migration in a Spring Boot app, add the Flyway dependency and configure your datasource. With Maven, include in pom.xml:
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-core</artifactId>
  <version>...latest...</version>
</dependency>
<dependency>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-database-postgresql</artifactId>  <!-- Optional: Postgres support -->
  <version>...latest...</version>
</dependency>
This gives your app Flyway support[10]. (If using Gradle, use implementation 'org.flywaydb:flyway-core'.)
Next, configure your application.yml (or application.properties) with your PostgreSQL settings and Flyway. For example:
spring:
  datasource:
    url: jdbc:postgresql://<RDS-endpoint>:5432/mydb
    username: mydb_user
    password: mydb_password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: none    # disable Hibernate's auto DDL; Flyway handles it
  flyway:
    enabled: true      # enable Flyway
    locations: classpath:db/migration  # (optional; default is db/migration)
This tells Spring Boot to run Flyway automatically at startup. The Flyway config reads your JDBC URL, user, and password just like any other datasource setting[11]. You usually disable Hibernate DDL (ddl-auto: none) so it doesn’t create/update schemas on its own, letting Flyway do it.
Now, when you start the Spring Boot application (e.g. mvn spring-boot:run or run the jar), Spring Boot will invoke Flyway.migrate() on startup[12]. In the logs, you should see Flyway output about creating its schema history table and applying migrations. For example:
... Flyway Community Edition ...
Creating Schema History table "PUBLIC"."flyway_schema_history"
Migrating schema "PUBLIC" to version 1 - init
Successfully applied 1 migration...
This means your V1__init.sql ran successfully and the database is updated. 🎉

Running Migrations with the Flyway CLI 💻

Besides auto-running on app startup, you can use the Flyway Command-Line Tool (CLI) separately. The CLI is useful for manual checks or CI pipelines. To use it: 1. Install Flyway CLI: Download from Flyway website or use a package manager.
2. Configure Flyway: Edit flyway.conf (or flyway.toml) with your DB connection, for example:
flyway.url=jdbc:postgresql://<RDS-endpoint>:5432/mydb
flyway.user=mydb_user
flyway.password=mydb_password
flyway.locations=filesystem:db/migration
3. Run migrate: In the terminal, run:
flyway migrate
Flyway will connect to the database, check the flyway_schema_history table, and apply any pending scripts just like it does in Spring. You’ll see output like:
Successfully validated 1 migration
Migrating schema "public" to version 1 - Create person table
Successfully applied 1 migration to schema "public"
(This output is from Flyway’s CLI example[13].)
The CLI also supports commands like flyway info (show status), flyway validate, flyway baseline, etc. It’s handy if you want to migrate without starting the Spring app, or in custom workflows (e.g., a CI job). The CLI uses the same scripts in db/migration and the same flyway_schema_history logic[13].

Common Issues & Troubleshooting ⚠️

•	Invalid SQL filename: Flyway requires the exact naming pattern (V + version + __ + description + .sql). A common mistake is using only one underscore (e.g. V1_init.sql) or wrong prefix. Flyway will throw an error like “Invalid SQL filenames found”. Always use two underscores and the proper V/U/R prefix[5][6]. For example, use V1__create_users.sql, not V1_create_users.sql.
•	Connection errors: If Flyway can’t connect to the database, check your configuration carefully. Common causes:
•	Wrong URL/credentials: Verify the JDBC URL, database name, username, and password. Even a trailing space or newline can break the connection[14].
•	AWS RDS network access: On AWS, ensure your RDS instance is publicly accessible (if you need to connect from outside the VPC) and that its security group allows your app’s IP on port 5432[15][16]. If you see a “Connection timed out”, it usually means the network/firewall is blocking access[15]. Edit the RDS security group to add an inbound rule (PostgreSQL/5432) from your IP or VPC subnet[16].
•	Database name issues: If you get “FATAL: database name does not exist”, double-check the database name in the URL. AWS RDS often creates a default database (commonly postgres) if you didn’t specify one. Make sure your URL’s /dbname matches an existing database.
•	Permission errors: Ensure the database user has the right privileges (CONNECT, CREATE, ALTER) on the schema. You can grant connect access in Postgres with: GRANT CONNECT ON DATABASE mydb TO mydb_user;.
By checking these things, you can avoid the most common migration hiccups.

Connect to AWS RDS and Run the Migration 🔗

Finally, here are the physical steps to hook up your Flyway-ready Spring Boot app to an AWS RDS PostgreSQL and run the migrations:
1.	Create an AWS RDS PostgreSQL instance: In the AWS Console, go to RDS, choose “Create database”, select PostgreSQL, and configure the instance (version, instance size, storage, etc.). Choose an existing VPC/security group or create one. Make sure “Public accessibility” is Yes if you need to connect from your local machine[15].
2.	Configure security group: Edit the RDS instance’s security group to allow inbound traffic on port 5432. For testing, you can set the source to “My IP” in the AWS console, which allows your current IP address to reach the database[16]. In production, restrict it to your application’s network or VPC.
3.	Note the endpoint and credentials: Once the RDS is available, grab its endpoint/host name and port from the RDS details. Also note the Master username and password you set up. You’ll use these in your Spring Boot app config.
4.	Update Spring Boot config: In your application.yml (or .properties), set spring.datasource.url to jdbc:postgresql://<your-rds-endpoint>:5432/<your-db-name>, and set username and password accordingly. For example:
 	spring:
  datasource:
    url: jdbc:postgresql://mydb.xxxxxxxx.us-east-1.rds.amazonaws.com:5432/myappdb
    username: mydbuser
    password: secretpassword
  flyway:
    enabled: true
    # (no need to change locations if using default)
 	If your RDS already has tables (e.g. a baseline schema), consider adding spring.flyway.baseline-on-migrate: true so Flyway marks the existing schema as version 1[17].
5.	Run the application: Start your Spring Boot app (e.g. ./mvnw spring-boot:run or run the jar). Flyway will connect to RDS and apply migrations. Watch the logs for Flyway messages. If all goes well, you’ll see the new tables/data in your AWS Postgres database.
6.	Verify the migration: You can connect to the RDS Postgres (using psql, DBeaver, or the AWS Console “Query Editor”) and check that your tables exist and flyway_schema_history is populated. Everything should match what your SQL scripts defined.
By following these steps, your Spring Boot app should now be using AWS RDS PostgreSQL, with Flyway automatically migrating the schema on startup. 🎉
