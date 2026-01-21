8.3 ☁️ AWS Elastic Beanstalk – Concepts, Why & Architecture
🤔 What is AWS Elastic Beanstalk?
AWS Elastic Beanstalk (EB) is a managed service that helps us deploy and run applications easily.

In simple words:

You give your Spring Boot application → Elastic Beanstalk runs it on AWS without you managing servers.

You focus on code, AWS handles infrastructure.

❓ Why Elastic Beanstalk is Needed

When we deploy a Spring Boot app to production, many questions come:

*Where will my app run?
*How to create server?
*How to restart app if it crashes?
*How to handle more users?
*How to monitor errors?
*How to secure AWS access?
Doing all this manually using EC2 is hard for beginners.
Elastic Beanstalk solves all these problems automatically.

🎯 What Elastic Beanstalk Actually Does

Elastic Beanstalk is not a server.
It is a manager that creates and controls AWS services for us.

Behind the scenes, EB automatically creates and manages:

*EC2 instance
*Load Balancer
*Auto Scaling
*Security Groups
*CloudWatch logs
*IAM roles

Now let’s understand each one clearly 👇

🖥️ EC2 Instance (Server)
What is EC2?

EC2 is a virtual server in AWS.

Just like:

*Your laptop runs apps locally
*EC2 runs apps in the cloud

Why EC2 is important in Elastic Beanstalk?

*Your Spring Boot app runs inside EC2
*Java, Maven, OS are installed here
*If EC2 stops → app stops

Elastic Beanstalk creates EC2 automatically, you don’t create it manually.

⚖️ Application Load Balancer (ALB)
What is Load Balancer?

A Load Balancer:
*Receives user requests
*Sends them to correct EC2 instance

Example:

*100 users request your app
*Load balancer distributes requests evenly

Why it is important?

*Prevents server overload
*Helps in scaling
*Keeps app available even if one EC2 fails

Elastic Beanstalk connects Load Balancer to your EC2 automatically.

📈 Auto Scaling Group
What is Auto Scaling?

Auto Scaling means:

*Increase servers when traffic increases
*Decrease servers when traffic reduces

Why it is important?

*If users increase suddenly → app should not crash
*Saves cost during low traffic
*Improves performance

Elastic Beanstalk manages:

*Minimum EC2
*Maximum EC2
*Scaling rules
You don’t write scaling logic manually.

🔐 Security Groups
What is Security Group?

Security Group is a firewall for AWS resources.

It controls:

*Which ports are open
*Who can access your server

Example:

*Allow HTTP (80)
*Allow HTTPS (443)
*Allow DB access only from EC2

Why it is important?

*Prevents unauthorized access
*Protects application & database

Elastic Beanstalk automatically:

*Creates security groups
*Attaches them to EC2 & Load Balancer

📊 CloudWatch Logs
What is CloudWatch?

CloudWatch is AWS monitoring service.

It collects:

*Application logs
*Error logs
*CPU usage
*Memory metrics

Why it is important?

*Debug production issues
*Monitor app health
*Track crashes & slow responses

Elastic Beanstalk:

*Sends app logs to CloudWatch
*Allows you to view logs from AWS Console

🪪 IAM Role
What is IAM Role?

IAM Role is a permission identity.

It defines:

*What AWS service can access
*What actions are allowed

Why it is important?
Elastic Beanstalk EC2 needs permissions to:

*Read environment variables
*Write logs to CloudWatch
*Access S3
*Connect to RDS

Instead of hardcoding credentials:

*IAM Role gives secure access

Elastic Beanstalk automatically attaches IAM roles.

🏗️ How Elastic Beanstalk Looks in AWS Cloud (Architecture View)

When you open AWS Console, Elastic Beanstalk looks like one service, but internally it creates this:

User
 ↓
Application Load Balancer
 ↓
Auto Scaling Group
 ↓
EC2 Instance (Spring Boot App)
 ↓
RDS Database


All these are real AWS resources, but EB hides complexity.

🔄 Internal Working Flow (Simple)

1️⃣ You upload Spring Boot JAR
2️⃣ EB creates environment
3️⃣ EC2 is launched
4️⃣ Java platform is installed
5️⃣ App is started
6️⃣ Load Balancer routes traffic
7️⃣ Logs go to CloudWatch
8️⃣ IAM handles permissions

Everything is automated.

🧠 Important Reality Check

Elastic Beanstalk is:

✅ Beginner friendly
✅ Production capable
✅ Free Tier friendly
✅ Fast deployment

But:

❌ Less control than raw EC2
❌ Not ideal for very complex infrastructure

