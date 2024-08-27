# 

# Animal Picture App

## Below instructions show how I got started with Camunda and tried to design BPMN process and write a simple client which deploys the process.

### Getting started with Animal Picture App

- Sign up for a Free Trial Account using Camuda SAS, - [camunda.io/signup](camunda.io/signup)
- Create a Camunda 8 Cluster
- Use Camunda 8 Web Modeller to design to create a New Project.
- In the **New Project**, create a new BPMN Process and design and implement the process.

The finished BPMN Process should look like the below:

![animal-picture.png](https://res.craft.do/user/full/30730f0e-25fc-f6cd-350b-ab4ce0341877/doc/B376081A-D7F4-4ADB-99E0-67B5FEA49719/D3165C92-9FF5-4152-9CD3-D394B58F000E_2/y6oxnysy114UENkiPZMmBkJd2mHgfRpf7JhhWYQjoP8z/animal-picture.png)

In the above BPMN process, User Task is used to enable the user to select the Animal Type out of Cats, Dogs and Bears.

The user has to select any one of the values.

An Exclusive Gateway is used to route the next sequence flow based on what the user selected.

For example, if the user selected **Dogs, then the sequence flow invokes a Service Task and call the Spring Boot Custom Client Application that fetches random images from Dogs API.**

### Running the Animal Picture App BPMN Process

You can deploy and test the BPMN Process from Camunda 8 using **Deploy** and **Run** from the Camunda 8 Web Modeller.

### Running the Spring Boot Client Application

***Pre-requisite: Ensure that you the Camunda Cluster running.***

The project is a maven spring boot project which you can run using command:

```bash
mvn spring-boot:run
```

The below class deploys the `animal-picture.bpmn` process

```bash
@SpringBootApplication
@Deployment(resources = "classpath:animal-picture.bpmn")
public class AnimalPictureApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnimalPictureApplication.class, args);
	}
}
```

Run the BPMN Process from the Camunda 8 SaaS.  The process waits for user input to select an animal type and then the code fetches the Rest API to get the image response.

### Containerize the Application

1. A **Containerfile** is created at the root of the project directory with the below contents

```bash
FROM openjdk:17-jdk

# Set the working directory
WORKDIR /app

# Copy the jar file to the working directory
COPY /target/animal-picture-0.0.1-SNAPSHOT.jar ./animal-picture-0.0.1-SNAPSHOT.jar

# Expose the port that the application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "animal-picture-0.0.1-SNAPSHOT.jar"]
```

2. Build the Docker image

```other
podman build -t quay.io/vgohel/animal-picture .
```

This command builds a Docker image with the tag `animal-picture-app`.

3. Run the Docker container

```other
podman run -p 8080:8080 quay.io/vgohel/animal-picture
```

### Creating HELM Charts

Use the below commands to create HELM charts and modify values.yaml

Go into the directory at the root of the project and execute:

```bash
vgohel@vgohel-mac ~ % helm create animal-picture-helm-chart
Creating animal-picture-helm-chart
```

```bash
vgohel@vgohel-mac animal-picture-helm-chart % cat values.yaml 
# Default values for animal-picture-helm-chart.
# This is a YAML-formatted file.
# Declare variables to be passed into your templates.

replicaCount: 1

image:
  repository: quay.io/vgohel/animal-picture
  tag: latest
  pullPolicy: IfNotPresent
  # Overrides the image tag whose default is the chart appVersion.
  tag: ""
```

```bash
vgohel@vgohel-mac animal-picture % tree animal-picture-helm-chart
animal-picture-helm-chart
├── Chart.yaml
├── charts
├── templates
│   ├── NOTES.txt
│   ├── _helpers.tpl
│   ├── deployment.yaml
│   ├── hpa.yaml
│   ├── ingress.yaml
│   ├── service.yaml
│   ├── serviceaccount.yaml
│   └── tests
│       └── test-connection.yaml
└── values.yaml
```

```bash
vgohel@vgohel-mac animal-picture % helm install animal-picture /Users/vgohel/Camunda/animal-picture/animal-picture-helm-chart-0.1.0.tgz
NAME: animal-picture
LAST DEPLOYED: Mon Aug 26 18:57:54 2024
NAMESPACE: default
STATUS: deployed
REVISION: 1
NOTES:
1. Get the application URL by running these commands:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=animal-picture-helm-chart,app.kubernetes.io/instance=animal-picture" -o jsonpath="{.items[0].metadata.name}")
  export CONTAINER_PORT=$(kubectl get pod --namespace default $POD_NAME -o jsonpath="{.spec.containers[0].ports[0].containerPort}")
  echo "Visit http://127.0.0.1:8080 to use your application"
  kubectl --namespace default port-forward $POD_NAME 8080:$CONTAINER_PORT
```

### Fetching Images and storing it in DB

I have not attempted this.

This can be done with the following:

We can include a sample H2 database dependency for testing purposes. Or we can have a DB instances running and try to connect to the existing DB schema.

```bash
<dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
```

In the application.properties file, connect to DB using the below properties:

```bash
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

Create a DTO class, using getters and setters methods. The variables can be **ID** and the **imageURL.** Example code below:

```bash
@Entity
public class ExtractImageDTO {

    @Id
    private Long id;
    private String imageUrl;

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
```

### Testing the Application

I have not done any testing in the code for this project.

I referred to this doc, [https://docs.camunda.io/docs/components/best-practices/development/testing-process-definitions/](https://docs.camunda.io/docs/components/best-practices/development/testing-process-definitions/) but I need more time to follow and understand the API's.

