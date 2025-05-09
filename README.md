## In loving Memory

🪦 10.04.2025 Josia

## horsey-go-brrrr-be

Backend for 4th-semester DHBW Web technologies project 'horsey-go-brrrr'. <br>
Initially, this was planned to be a multiplayer chess app. Due to the unfortunate reality of the development team being halved, it has been scaled down to a 4-in-a-row game.
Nevertheless, the name shall remain as a glorious tribute to those whomst have fallen, for they shall never be forgotten.

## Local setup

First, start the postgresql docker container by running the provided docker compose file.

```bash
docker compose up -d
```

- Requirements:
  - Java 21
  - Maven 3.8

```bash
./mvnw quarkus:dev
```

## Resources

### JEN doc

[jen.md](doc/jen.md)

### Swagger UI

The project ships with a swagger UI. This is accessible under
[http://http://localhost:8080/api/q/swagger-ui/#/](http://http://localhost:8080/api/q/swagger-ui/#/) when running the project in dev mode.

### erm

![](doc/erm.png)
