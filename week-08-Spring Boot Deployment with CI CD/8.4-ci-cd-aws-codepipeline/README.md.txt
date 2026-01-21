CI/CD for Spring Boot on AWS (Free Tier)
This guide shows how to set up a simple CI/CD pipeline for a Spring Boot app using AWS Free Tier. We use AWS CodePipeline (source and orchestration), CodeBuild (build/test), and Elastic Beanstalk (EB) (deploy) together. The source code is hosted on GitHub. The pipeline will automatically build and deploy on every commit. We use beginner-friendly language and emojis to explain each step.

🔄 Pipeline Overview
The code flow is: GitHub → CodePipeline → CodeBuild → Elastic Beanstalk. In simple terms:
GitHub (source code) --> CodePipeline (source stage) --> CodeBuild (build/test stage) --> Elastic Beanstalk (deploy stage) 
•	GitHub: You push code (e.g. a commit).
•	CodePipeline: Detects the new commit and starts a pipeline run. It has three stages: Source, Build, Deploy.
•	CodeBuild: In the Build stage, AWS compiles and tests your Spring Boot app using a buildspec.yml. If the build fails, the pipeline stops here and no deploy happens. If it passes, CodeBuild produces a JAR file (artifact).
•	Elastic Beanstalk: In the Deploy stage, CodePipeline takes the artifact from CodeBuild and updates the EB environment. Elastic Beanstalk then deploys the new version to EC2 instances.
This way, every GitHub push (to the chosen branch) triggers CodePipeline, which builds and (if successful) deploys your app to EB[1][2]. For example, once set up, merging a GitHub PR automatically updates the running app.

📥 Connect GitHub to CodePipeline

In the AWS Console, go to CodePipeline → Create pipeline. Give the pipeline a name. Under Source provider, choose GitHub (Version 2). Then click Connect to GitHub. AWS will open a GitHub authorization window – login if needed and allow AWS access to your repo[3]. After connection, select the correct repository and branch (e.g. main). Make sure “Start the pipeline on source code change” is checked – this creates a webhook so that new commits automatically start the pipeline[2].

👉 Emojis note: In simple terms, connecting GitHub means AWS is allowed to fetch your code. The checkbox for “start on change” means AWS will listen for new commits and auto-run the pipeline.
Permissions: CodePipeline will create (or you can create) a service role. This IAM role needs permission to read your code and write artifacts (usually S3 access). AWS can create a basic role automatically. You may need to edit it later to allow full Elastic Beanstalk access (see IAM section below).

⚙️ Configure CodeBuild and buildspec.yml

In the same pipeline setup, add a Build stage using AWS CodeBuild. If you haven’t created a CodeBuild project yet, click “Create project”. Give it a name, and set the Environment (e.g. Amazon Linux, using a standard image with Java/Maven). Most importantly, tell CodeBuild to use a buildspec file from your source: a YAML file named buildspec.yml in your project root[4][5].
A buildspec is simply a YAML script with the build commands for CodeBuild[6]. For a Spring Boot app, your buildspec.yml might look like:
version: 0.2
phases:
  install:
    commands:
      - echo "Installing dependencies..."
      - mvn install -DskipTests
  build:
    commands:
      - echo "Building the Spring Boot JAR..."
      - mvn package
artifacts:
  files:
    - target/*.jar
This tells CodeBuild to run Maven and produce a JAR (in target/). The artifacts section then collects that JAR so CodePipeline can pass it to the deploy stage. You can customize phases (e.g. test, post_build) and environment variables as needed.
AWS Docs define buildspec as: “a collection of build commands and related settings, in YAML format, that CodeBuild uses to run a build.”[6][5]. By default the file is named buildspec.yml in your code root[4]. CodeBuild needs an IAM service role too – typically it needs permission to read the source artifact from S3 (provided by CodePipeline) and write back the build artifact. AWS can create a new role for you; just ensure it has S3 access and (if using CodeArtifact) CodeArtifact permissions (see below).

☁️ Deploy to Elastic Beanstalk

Elastic Beanstalk (EB) is our deployment target. EB automatically creates and manages resources (EC2, Load Balancer, auto-scaling, etc.) for your app[7][8]. In AWS Console, create an EB Application and Environment first (choose Java or Tomcat platform since this is a Spring Boot app). AWS will spin up an EC2 instance, security group, Auto Scaling group, S3 bucket, etc. as part of the EB environment[8].
In CodePipeline, add a Deploy stage with provider AWS Elastic Beanstalk. Select your EB Application name and Environment. CodePipeline will then take the artifact from CodeBuild and send it to EB. EB will update the application version and deploy the new JAR to the running instance(s). You can later check the EB dashboard or visit your app’s URL to see the updated app.
Note: EB itself is free, but you pay for the AWS resources it uses (e.g. the EC2 instance). Under the Free Tier, you get 750 hours/month of t2.micro EC2 and 5 GB S3, so a small EB app can stay free for a while if those limits aren’t exceeded.

🔒 IAM Roles & Permissions

•	CodePipeline service role: AWS will set up or let you create a service role (with codepipeline.amazonaws.com principal). This role must have permissions to interact with your resources: at least S3 (for artifacts), CodeBuild, and Elastic Beanstalk. In particular, for EB deployments you often need extra ELB/EB actions. AWS docs note that the CodePipeline role should allow Elastic Beanstalk actions (including elasticbeanstalk:DescribeEvents) or the deploy will hang[9][10]. If you get a “provided role does not have sufficient permissions” error, attach the AWS-managed AWSElasticBeanstalkFullAccess policy, or at least add elasticbeanstalk:* and related ec2, elasticloadbalancing, and autoscaling actions.
•	CodeBuild service role: When creating the CodeBuild project, you also specify a service role (with codebuild.amazonaws.com principal). This role needs permission to pull source from S3/CodePipeline and push build artifacts back to S3. If your build accesses other services (e.g. S3, KMS), grant those. You can use AWS managed policies like AmazonS3FullAccess for simplicity. If you were to use CodeArtifact, CodeBuild would also need CodeArtifact permissions – but we won’t use it here.
•	EB EC2 instance role (instance profile): EB itself requires an instance profile for its EC2 instances. AWS provides a default role aws-elasticbeanstalk-ec2-role. Ensure this role has the managed policies AWSElasticBeanstalkWebTier and AWSElasticBeanstalkWorkerTier attached[11]. These give your EC2 instances permission to, for example, upload logs to S3 or pull Docker images if needed. (If using the console defaults, AWS usually creates this role for you with those policies.)
In summary, give CodePipeline full access to S3 and EB (via policies or AWSCodePipelineFullAccess), CodeBuild full S3 access, and attach the AWSEB policies to your EB instance role[9][11]. This avoids common permission errors in the pipeline or EB console.

🆓 AWS Free Tier & CodeArtifact Notes

All the steps above can use AWS Free Tier resources, but be mindful of limits:
•	CodePipeline: Free Tier gives 1 free active pipeline per month (V1 pipeline) and 100 free action-minutes per month for V2 pipelines[12]. A pipeline that’s idle (no changes) costs nothing.
•	CodeBuild: Free Tier includes 100 build-minutes per month on general-purpose instances (e.g. Linux 3.0 or Linux 5.0 images) and 6,000 build-seconds per month on AWS Lambda builds[13]. If you exceed this, you pay by the second/minute of build time.
•	CodeArtifact: AWS CodeArtifact (private artifact repository) is Free Tier for up to 2 GB storage and 100,000 requests per month[14]. This means small use is free. However, beginners sometimes encounter a SubscriptionRequiredException (or “OptInRequired”) error when trying to use CodeArtifact without enabling it[15]. This error basically means your account hasn’t “subscribed” to the service (or you need a support plan), which can be confusing. Important: For a basic Spring Boot pipeline, you usually don’t need CodeArtifact at all. Your build pulls public Maven dependencies directly, and the built JAR goes to S3/EB. So you can safely ignore CodeArtifact, avoiding that error entirely[14][15].
•	Others: Elastic Beanstalk itself has no extra charge, but uses EC2/S3. Under Free Tier you have 750h of t2.micro EC2 per month and 5GB S3 (for 12 months). Going over these (or beyond year one) will incur costs.
In short, everything we use (CodePipeline, CodeBuild, EB) has a free tier allowance[13][12]. CodeArtifact is free in small amounts[14] but not needed here – and the subscription error can be ignored for our simple use case.

⚠️ Real-World Issue: CodeArtifact “SubscriptionRequiredException”

Some users see an error like SubscriptionRequiredException or “The AWS access key ID needs a subscription for the service” when CodeBuild tries to use CodeArtifact[15]. This comes from the AWS SDK indicating you must enable (subscribe to) the service first. It is not an error in your code, but rather AWS telling you that CodeArtifact isn’t set up on your account. As mentioned, you don’t need CodeArtifact for a basic Spring Boot build — your mvn package pulls dependencies from Maven Central, and your artifact goes directly to EB. So you can safely remove any CodeArtifact login from your buildspec. This error will then disappear. In other words, CodeArtifact is optional; skipping it avoids the subscription error, and still works fine on the Free Tier[14][15].
👷 Implementation Steps (GitHub → CodePipeline → CodeBuild → EB)
1.	Create EB Application & Environment: In the AWS Console, open Elastic Beanstalk and Create Application. Give it a name (e.g. MySpringApp) and choose a Java platform. AWS will create a new environment (e.g. “my-spring-env”), automatically provisioning EC2, S3, etc.[8]. Wait until the environment is green (launched).
2.	Prepare GitHub Repo: Push your Spring Boot code (with pom.xml or build.gradle and your buildspec.yml in the root) to a GitHub repository. Ensure the buildspec.yml has the correct build commands and artifacts as shown above.
3.	Create CodeBuild Project (if not done via pipeline): In AWS CodeBuild, create a project. Choose the right OS/runtime (e.g. Amazon Linux, Java, Maven). For Environment, pick “Use a buildspec file” so it reads your repo’s buildspec.yml. Note the Service role (IAM) it creates – ensure this role can write to S3 (so artifacts can be stored).
4.	Create CodePipeline Pipeline: Go to AWS CodePipeline and create a pipeline. Set Source = GitHub (Version 2), connect as described. For Build stage, choose AWS CodeBuild and select the project you made (or let CodePipeline create one for you). For Deploy stage, choose AWS Elastic Beanstalk and select your EB Application and Environment names (from step 1). CodePipeline will also need (or create) a service role – accept the defaults or ensure it has S3, CodeBuild, and EB permissions.
5.	Trigger the Pipeline: Commit and push some change to your GitHub branch. CodePipeline should automatically start (check the console). Watch the Source stage (it pulls code), then the Build stage (CodeBuild runs Maven).
6.	If the Build fails, you’ll see an error in CodeBuild logs. Fix any compile/test errors in your code or adjust buildspec.yml, commit again, and the pipeline will retry.
7.	If the Build succeeds, CodePipeline moves to the Deploy stage. It will bundle the artifact and tell EB to deploy. EB will then show a new application version and deploy it.
8.	Verify Deployment: After a successful deploy, your Spring Boot app is live. You can go to the EB environment URL (shown in the EB console) to see your app. Each push to GitHub will repeat this process: pipeline builds and (if OK) updates the running app.
What Happens on Fail/Pass: In short, if CodeBuild fails, CodePipeline stops at Build and does not touch Elastic Beanstalk. You must fix the build errors (as reported in logs) and push again. If CodeBuild passes, CodePipeline automatically deploys the new JAR to EB, replacing the old version. You’ll see status and logs in the AWS consoles for each step.
🙏 Happy coding! With this setup, your Spring Boot app goes from code to cloud automatically, using AWS Free Tier services. 
