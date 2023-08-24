# Redpanda + Spring Boot

Using Redpanda with Spring Boot and Apache Avro.

# Run

```shell
docker-compose up -d
mvn spring-boot:run
```

# View

Browse to Console at: http://localhost:8080

![Redpanda Console Topic View](./topic.png)
![Redpanda Console Schema Registry View](./schema.png)

# Shutdown

```shell
docker-compose down -v
```
