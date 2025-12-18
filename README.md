# Bed & Breakfast Room Management System (Spring Boot + Config Server)

This repo contains a Spring Cloud microservice suite under `bnb-system/`:

- `config-server` (Spring Cloud Config Server) — central configuration and service URLs
- `booking-service` — rooms, guests, bookings, stay duration options, service packages
- `payment-service` — payment records (including **CASH**) and capture flow
- `access-service` — smartlock access grants/revokes (stub smartlock client) + SMS notification trigger
- `notification-service` — SMS send API (stub gateway) + audit log

## Local dev (no Docker)

In separate terminals:

```bash
mvn -f bnb-system/config-server/pom.xml spring-boot:run
mvn -f bnb-system/notification-service/pom.xml spring-boot:run
mvn -f bnb-system/access-service/pom.xml spring-boot:run
mvn -f bnb-system/booking-service/pom.xml spring-boot:run
mvn -f bnb-system/payment-service/pom.xml spring-boot:run
```

Default ports (from Config Server):

- Config Server: `8888`
- Booking: `8081`
- Payment: `8082`
- Access: `8083`
- Notification: `8084`

## Docker Compose

```bash
docker compose -f bnb-system/docker-compose.yml up --build
```
