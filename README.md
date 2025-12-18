# Bed & Breakfast Room Management System (Spring Boot + Config Server)

This repo contains a Spring Cloud microservice suite under `bnb-system/`:

- `config-server` (Spring Cloud Config Server) — central configuration and service URLs
- `discovery-service` (Eureka server) — optional service discovery dashboard (not required by the current gateway routing)
- `gateway-service` (Spring Cloud Gateway) — single entrypoint that routes `/api/*` to the backing services
- `booking-service` — rooms, guests, bookings, stay duration options, service packages
- `payment-service` — payment records (including **CASH**) and capture flow
- `access-service` — smartlock access grants/revokes (stub smartlock client) + SMS notification trigger
- `notification-service` — SMS send API (stub gateway) + audit log

## Local dev (no Docker)

Start services in this order (separate terminals). Anything that imports config from `http://localhost:8888` must come **after** the Config Server.

Database prerequisite: `booking-service`, `payment-service`, and `access-service` expect Postgres instances (defaults: `localhost:5433`, `localhost:5434`, `localhost:5435`). Easiest option is to run only the DB containers:

```bash
docker compose -f bnb-system/docker-compose.yml up booking-db payment-db access-db
```

```bash
# 1) Config (required)
mvn -f bnb-system/config-server/pom.xml spring-boot:run

# 2) Optional: Eureka dashboard (currently not required)
mvn -f bnb-system/discovery-service/pom.xml spring-boot:run

# 3) Backing services (order matters for inter-service calls)
mvn -f bnb-system/notification-service/pom.xml spring-boot:run
mvn -f bnb-system/booking-service/pom.xml spring-boot:run
mvn -f bnb-system/access-service/pom.xml spring-boot:run
mvn -f bnb-system/payment-service/pom.xml spring-boot:run

# 4) Gateway (routes to the services above)
mvn -f bnb-system/gateway-service/pom.xml spring-boot:run
```

Tip: if a service fails at boot because it can’t reach Config Server yet, wait until Config Server is up (`/actuator/health`) and restart the service.

Default ports:

- Config Server: `8888`
- Discovery (Eureka): `8761`
- Gateway: `8080`
- Booking: `8081`
- Payment: `8082`
- Access: `8083`
- Notification: `8084`

## Docker Compose

```bash
docker compose -f bnb-system/docker-compose.yml up --build
```

Compose brings up Postgres databases + services + the gateway. If you’re just trying to run everything locally, this is the easiest path.
