# Day 10 -- Docker & CI/CD (Sprint 9)

> TICKET-F108 through TICKET-F119

---

## Overview

Today you containerize SmartBudget and set up a CI/CD pipeline. You will:

1. Write a multi-stage Dockerfile for the Spring Boot backend
2. Write a multi-stage Dockerfile for the React frontend (with nginx)
3. Configure nginx as a reverse proxy
4. Orchestrate all services with docker-compose
5. Build a GitHub Actions CI/CD pipeline
6. Present a final demo

By the end of Day 10, SmartBudget runs as a fully containerized application with automated testing on every push.

---

## Key Concepts

- **Docker**: Packages your app + dependencies into a portable container
- **Multi-stage build**: Uses one stage to build, another to run (smaller final image)
- **nginx**: Web server that serves static files and proxies API requests
- **docker-compose**: Orchestrates multiple containers (frontend, backend, database)
- **GitHub Actions**: Automates build, test, and deploy on every git push
- **CI/CD**: Continuous Integration (build + test) / Continuous Delivery (deploy)

---

## Architecture with Docker

```
                    +------------------+
                    |   Browser        |
                    +--------+---------+
                             |
                             | Port 80
                             v
              +--------------+---------------+
              |         nginx (frontend)     |
              |  - Serves React static files |
              |  - Proxies /api/* to backend |
              +-------+---------------------+
                      |
                      | Port 8080 (internal)
                      v
              +-------+---------------------+
              |     Spring Boot (backend)    |
              |  - REST API                  |
              |  - JPA / Hibernate           |
              +-------+---------------------+
                      |
                      | Port 5432 (internal)
                      v
              +-------+---------------------+
              |      PostgreSQL (db)         |
              |  - Production database       |
              +-----------------------------+
```

All 3 services run in the same Docker network and communicate using container names as hostnames.

---

## Tickets

### TICKET-F108: Backend Dockerfile
**File:** `backend/Dockerfile`

**Description:** Write a multi-stage Dockerfile for the Spring Boot backend.

**What**
- A two-stage `backend/Dockerfile` — Maven image builds the JAR, JRE image runs it — producing an image under 300 MB.

**Why**
- Single-stage builds ship the JDK, Maven, source, and `.m2` cache to production — 700+ MB of attack surface.
- Multi-stage strips everything except the runtime and the JAR.

**Observe**
- `docker images smartbudget-backend` shows a size near 220 MB.
- `docker run -p 8080:8080 smartbudget-backend` boots, and `curl localhost:8080/actuator/health` returns `{"status":"UP"}`.

**Instructions (follow the detailed TODOs in backend/Dockerfile):**

**Stage 1 -- Build:**
```
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src ./src
RUN mvn package -DskipTests
```

**Stage 2 -- Run:**
```
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Key points:**
- Stage 1 uses a Maven image (has build tools), Stage 2 uses a JRE image (only runtime)
- `dependency:resolve` before copying src enables Docker layer caching
- `-DskipTests` because tests run in CI, not in the Docker build
- Final image only has the JAR file (no source code, no Maven)

**Acceptance Criteria:**
- [ ] `docker build -t smartbudget-backend ./backend` completes without errors
- [ ] `docker run -p 8080:8080 smartbudget-backend` starts the app
- [ ] http://localhost:8080/actuator/health returns `{"status":"UP"}`
- [ ] Final image size is under 300MB (check with `docker images`)
- [ ] No source code or Maven in the final image

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Two `FROM` lines = two stages. The first stage compiles your JAR using a Maven image. The second stage starts FROM a slim JRE image and `COPY --from=build` only pulls the JAR across. Final image has zero source code or Maven binaries — that's why it's small.

</details>

<details>
<summary><b>Hint 2 — Dockerfile</b></summary>

```dockerfile
# ─── Stage 1: build ──────────────────────────────────────
FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve              # cache layer
COPY src ./src
RUN mvn package -DskipTests

# ─── Stage 2: run ────────────────────────────────────────
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
```

Build + run:
```bash
docker build -t smartbudget-backend ./backend
docker run --rm -p 8080:8080 smartbudget-backend
curl http://localhost:8080/actuator/health   # {"status":"UP"}
```

</details>

<details>
<summary><b>Hint 3 — Production-grade Dockerfile</b></summary>

```dockerfile
# syntax=docker/dockerfile:1.6

# ─── Stage 1: build ──────────────────────────────────────
FROM maven:3.9-eclipse-temurin-25 AS build

WORKDIR /app

# Copy pom.xml first so dependency download is cached separately from source changes
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn -B dependency:resolve

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B package -DskipTests

# ─── Stage 2: run ────────────────────────────────────────
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Run as non-root for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-XX:+ExitOnOutOfMemoryError","-jar","app.jar"]
```

`docker images smartbudget-backend` → ~220 MB (JRE base + ~40 MB JAR). Compare with a naive single-stage build (Maven + source + JDK) → ~700+ MB.

**Caching trick:** because `pom.xml` is copied before `src/`, Docker reuses the dependency-resolution layer until your pom changes. Repeat builds become much faster.

**Run as non-root** — if the JVM is ever compromised, the attacker doesn't get root inside the container. Two-line cost, big security win.

</details>

---

### TICKET-F109: Frontend Dockerfile + nginx.conf
**File:** `frontend/Dockerfile` and `frontend/nginx.conf`

**Description:** Write a multi-stage Dockerfile for the React frontend with nginx.

**What**
- `frontend/Dockerfile` (Node build → nginx serve) plus `frontend/nginx.conf` that serves the SPA on port 80 and proxies `/api/*` and `/actuator/*` to `backend:8080`.

**Why**
- Node is only needed to *build* the bundle; production just serves static files.
- nginx also kills CORS by terminating `/api` on the same origin and forwarding internally.

**Observe**
- `docker run -p 80:80 smartbudget-frontend` serves the UI at `localhost`; a hard refresh on `/transactions` still loads the SPA (proof `try_files` works).
- `nginx -t` inside the container reports the config is valid.

**Instructions (follow the TODOs in both files):**

**frontend/Dockerfile:**
```
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

**frontend/nginx.conf:**
```
server {
    listen 80;

    location / {
        root /usr/share/nginx/html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://backend:8080;
    }

    location /actuator/ {
        proxy_pass http://backend:8080;
    }
}
```

**Key points:**
- `npm ci` is faster and more reliable than `npm install` for CI/Docker
- `try_files $uri /index.html` enables React Router (SPA client-side routing)
- `proxy_pass http://backend:8080` forwards API calls to the backend container
- `backend` is the container name (defined in docker-compose)

**Acceptance Criteria:**
- [ ] `docker build -t smartbudget-frontend ./frontend` completes
- [ ] nginx serves the React app on port 80
- [ ] `try_files` works: refreshing /transactions loads the SPA correctly
- [ ] `/api/*` requests are proxied to the backend
- [ ] Verify with `nginx -t` (inside the container) that config is valid

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Stage 1 = Node image, runs `npm ci` then `npm run build` (produces `dist/`). Stage 2 = nginx:alpine, copies `dist/` into `/usr/share/nginx/html` and your `nginx.conf` over the default. `try_files $uri /index.html` is the *single line* that makes React Router survive a hard refresh.

</details>

<details>
<summary><b>Hint 2 — Both files</b></summary>

`frontend/Dockerfile`:

```dockerfile
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

`frontend/nginx.conf`:

```nginx
server {
  listen 80;

  location / {
    root /usr/share/nginx/html;
    try_files $uri $uri/ /index.html;     # SPA fallback
  }

  location /api/ {
    proxy_pass http://backend:8080;       # `backend` = docker-compose service
    proxy_set_header Host $host;
  }
  location /actuator/ {
    proxy_pass http://backend:8080;
  }
}
```

Validate config inside the container: `docker exec <id> nginx -t`.

</details>

<details>
<summary><b>Hint 3 — Full files + gzip & caching</b></summary>

`frontend/Dockerfile`:

```dockerfile
# syntax=docker/dockerfile:1.6

# ─── Stage 1: build ──────────────────────────────────────
FROM node:22-alpine AS build
WORKDIR /app

COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# ─── Stage 2: nginx ──────────────────────────────────────
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget -qO- http://localhost/ || exit 1
```

`frontend/nginx.conf`:

```nginx
server {
  listen      80;
  server_name _;

  root  /usr/share/nginx/html;
  index index.html;

  # Cache static assets aggressively (they have hashed filenames from Vite)
  location ~* \.(?:js|css|woff2?|ico|png|jpg|svg)$ {
    expires 30d;
    add_header Cache-Control "public, immutable";
    try_files $uri =404;
  }

  # SPA fallback — every unknown path renders index.html
  location / {
    try_files $uri $uri/ /index.html;
  }

  # API proxy
  location /api/ {
    proxy_pass         http://backend:8080;
    proxy_http_version 1.1;
    proxy_set_header   Host              $host;
    proxy_set_header   X-Real-IP         $remote_addr;
    proxy_set_header   X-Forwarded-For   $proxy_add_x_forwarded_for;
  }
  location /actuator/ {
    proxy_pass http://backend:8080;
  }

  # gzip
  gzip on;
  gzip_types text/plain text/css application/json application/javascript text/javascript;
  gzip_min_length 1024;
}
```

Why `try_files`? Without it, `/transactions` hits nginx, nginx looks for `/transactions/index.html`, doesn't find it, returns 404 — your SPA never loads. With it, nginx falls back to `/index.html`, React Router picks up the URL, renders the right page.

Build + run isolated:
```bash
docker build -t smartbudget-frontend ./frontend
docker run --rm -p 80:80 smartbudget-frontend
# Browse http://localhost — UI loads, but /api fails (backend not present)
```

Real test comes with `docker-compose` in F110.

</details>

---

### TICKET-F110: docker-compose.yml -- Services
**File:** `docker-compose.yml` (project root)

**Description:** Define all 3 services in a docker-compose file.

**What**
- A root `docker-compose.yml` declaring three services — `db` (postgres:15-alpine), `backend` (built from `./backend`), `frontend` (built from `./frontend`) — plus a `pgdata` named volume.

**Why**
- Compose creates a private network where each service's name resolves as a hostname, so `backend` can reach `db:5432` and nginx can `proxy_pass http://backend:8080` with zero hardcoded IPs.
- One command brings the whole stack up.

**Observe**
- `docker-compose up` starts all three containers; `docker-compose ps` shows them as Up.
- `http://localhost` renders the React UI populated with data from PostgreSQL through the nginx proxy.

**Instructions (follow the detailed TODOs in docker-compose.yml):**

```yaml
version: "3.8"

services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB: smartbudget
      POSTGRES_USER: sb_user
      POSTGRES_PASSWORD: sb_pass
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://db:5432/smartbudget
      SPRING_DATASOURCE_USERNAME: sb_user
      SPRING_DATASOURCE_PASSWORD: sb_pass
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      - db

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  pgdata:
```

**Key points:**
- `db` is the hostname that the backend uses to connect to PostgreSQL
- `depends_on` ensures startup order (db -> backend -> frontend)
- Environment variables override application.properties settings
- `pgdata` volume persists database data across container restarts

**Acceptance Criteria:**
- [ ] `docker-compose build` builds all 3 images
- [ ] `docker-compose up` starts all 3 services
- [ ] http://localhost shows the React UI
- [ ] The UI displays real data from the API (backend connected to PostgreSQL)
- [ ] `docker-compose down` stops everything cleanly

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

3 services: `db`, `backend`, `frontend`. Each container can reach another by service name (so `backend` connects to `db:5432`, `frontend`'s nginx proxies to `backend:8080`). A named volume keeps PostgreSQL data alive across `docker-compose down`.

</details>

<details>
<summary><b>Hint 2 — File</b></summary>

```yaml
version: "3.8"

services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB:       smartbudget
      POSTGRES_USER:     sb_user
      POSTGRES_PASSWORD: sb_pass
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports: ["5432:5432"]

  backend:
    build: ./backend
    environment:
      SPRING_PROFILES_ACTIVE:        prod
      SPRING_DATASOURCE_URL:         jdbc:postgresql://db:5432/smartbudget
      SPRING_DATASOURCE_USERNAME:    sb_user
      SPRING_DATASOURCE_PASSWORD:    sb_pass
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on: [db]
    ports: ["8080:8080"]

  frontend:
    build: ./frontend
    depends_on: [backend]
    ports: ["80:80"]

volumes:
  pgdata:
```

Boot:
```bash
docker-compose build
docker-compose up        # leave running
# in another terminal:
curl http://localhost/api/transactions   # JSON returned through nginx proxy
open http://localhost                    # React UI
```

</details>

<details>
<summary><b>Hint 3 — Full file + tour</b></summary>

```yaml
# docker-compose.yml
version: "3.8"

services:
  db:
    image: postgres:15-alpine
    container_name: smartbudget-db
    environment:
      POSTGRES_DB:       smartbudget
      POSTGRES_USER:     sb_user
      POSTGRES_PASSWORD: sb_pass
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports:
      - "5432:5432"                # expose to host (optional, handy for pgAdmin)

  backend:
    build: ./backend
    container_name: smartbudget-backend
    environment:
      SPRING_PROFILES_ACTIVE:        prod
      SPRING_DATASOURCE_URL:         jdbc:postgresql://db:5432/smartbudget
      SPRING_DATASOURCE_USERNAME:    sb_user
      SPRING_DATASOURCE_PASSWORD:    sb_pass
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
      SPRING_SQL_INIT_MODE:          never        # don't reseed in prod
    depends_on:
      - db
    ports:
      - "8080:8080"

  frontend:
    build: ./frontend
    container_name: smartbudget-frontend
    depends_on:
      - backend
    ports:
      - "80:80"

volumes:
  pgdata:
```

**The docker network is implicit.** Compose creates a private network where each service's hostname is its key (`db`, `backend`, `frontend`). The backend's JDBC URL says `jdbc:postgresql://db:5432/...` — `db` resolves inside the network. The nginx proxy uses `http://backend:8080` for the same reason.

**Verify:**
```bash
docker-compose build          # ~2 min on a cold start, ~30s with cache
docker-compose up             # foreground; Ctrl+C to stop
docker-compose ps             # 3 services should show "Up"

curl http://localhost/api/transactions      # JSON from Spring through nginx
open http://localhost                       # React UI
docker-compose down                         # stops everything; volumes preserved
```

`SPRING_*` env vars override `application.properties` because Spring's `RelaxedBinder` converts them automatically (`SPRING_DATASOURCE_URL` → `spring.datasource.url`).

Use `SPRING_PROFILES_ACTIVE=prod` to load `application-prod.properties` (PostgreSQL config, no H2 console).

</details>

---

### TICKET-F111: docker-compose.yml -- Health Checks
**File:** `docker-compose.yml`

**Description:** Add health checks and production-ready configuration.

**What**
- A `healthcheck` on the `db` service plus `depends_on: condition: service_healthy` on `backend`, and `restart: unless-stopped` across all three services.

**Why**
- Plain `depends_on` only waits for the container *process* to start — Postgres needs another ~3s to accept connections, and the backend crashes if it tries to connect too early.
- Health-gated startup and auto-restart turn the stack from "works on first try" into "self-healing".

**Observe**
- `docker-compose ps` shows `(healthy)` next to the db row; backend logs wait for ~3 s before printing "Started SmartBudgetApplication".
- `docker kill smartbudget-backend` is followed by an automatic restart within 5 seconds.

**Instructions:**
1. Add a healthcheck to the db service:
   ```yaml
   healthcheck:
     test: ["CMD-SHELL", "pg_isready -U sb_user -d smartbudget"]
     interval: 5s
     timeout: 5s
     retries: 5
   ```
2. Update backend `depends_on` to wait for healthy db:
   ```yaml
   depends_on:
     db:
       condition: service_healthy
   ```
3. Add restart policy: `restart: unless-stopped` to all services

**Acceptance Criteria:**
- [ ] `docker-compose up` waits for PostgreSQL to be healthy before starting backend
- [ ] `docker-compose ps` shows health status for the db service
- [ ] If the backend crashes, it automatically restarts

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`depends_on: [db]` only waits for the container to *start* — Postgres needs another ~3s to accept connections. Use `depends_on: condition: service_healthy` + a `healthcheck` on `db` so backend actually waits. Add `restart: unless-stopped` to all three services so a crash auto-recovers.

</details>

<details>
<summary><b>Hint 2 — The two snippets</b></summary>

```yaml
db:
  # ... existing config
  healthcheck:
    test: ["CMD-SHELL", "pg_isready -U sb_user -d smartbudget"]
    interval: 5s
    timeout: 5s
    retries: 5
  restart: unless-stopped

backend:
  # ...
  depends_on:
    db:
      condition: service_healthy
  restart: unless-stopped

frontend:
  # ...
  depends_on:
    - backend
  restart: unless-stopped
```

</details>

<details>
<summary><b>Hint 3 — Full file with all healthchecks</b></summary>

```yaml
version: "3.8"

services:
  db:
    image: postgres:15-alpine
    environment:
      POSTGRES_DB:       smartbudget
      POSTGRES_USER:     sb_user
      POSTGRES_PASSWORD: sb_pass
    volumes:
      - pgdata:/var/lib/postgresql/data
    ports: ["5432:5432"]
    healthcheck:
      test:     ["CMD-SHELL", "pg_isready -U sb_user -d smartbudget"]
      interval: 5s
      timeout:  3s
      retries:  10
      start_period: 5s
    restart: unless-stopped

  backend:
    build: ./backend
    environment:
      SPRING_PROFILES_ACTIVE:        prod
      SPRING_DATASOURCE_URL:         jdbc:postgresql://db:5432/smartbudget
      SPRING_DATASOURCE_USERNAME:    sb_user
      SPRING_DATASOURCE_PASSWORD:    sb_pass
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      db:
        condition: service_healthy
    ports: ["8080:8080"]
    healthcheck:
      test:     ["CMD", "wget", "-qO-", "http://localhost:8080/actuator/health"]
      interval: 10s
      timeout:  5s
      retries:  5
      start_period: 30s
    restart: unless-stopped

  frontend:
    build: ./frontend
    depends_on:
      backend:
        condition: service_healthy
    ports: ["80:80"]
    restart: unless-stopped

volumes:
  pgdata:
```

Verify:
```bash
docker-compose down -v
docker-compose up
# backend logs wait ~3s, then "Started SmartBudgetApplication"
docker-compose ps
# NAME                       STATUS
# smartbudget-db             Up 30 seconds (healthy)
# smartbudget-backend        Up 25 seconds (healthy)
# smartbudget-frontend       Up 24 seconds
```

`restart: unless-stopped` recovers from any crash UNLESS you `docker-compose stop` (so you can intentionally stop services). For a brutal demo: `docker kill smartbudget-backend` → it bounces back in 5 seconds.

</details>

---

### TICKET-F112: Test Docker Deployment
**File:** N/A

**Description:** Test the full Docker deployment end-to-end.

**What**
- A full smoke test from a clean slate — `down -v`, `build --no-cache`, `up`, walk the UI, then `down` + `up` again to prove the volume preserves data.

**Why**
- The first time you build clean is when integration bugs surface (missing env var, wrong proxy target, stale image cache).
- Catching them here is much cheaper than catching them on the demo laptop in front of the room.

**Observe**
- Transactions added through the UI appear in `curl http://localhost/api/transactions`.
- After `docker-compose down` (no `-v`) and `up`, the same transactions are still there — proof the `pgdata` volume survives container teardown.

**Instructions:**
1. `docker-compose down -v` (clean start)
2. `docker-compose build --no-cache` (fresh build)
3. `docker-compose up`
4. Test sequence:
   - http://localhost -- React UI loads
   - Add a transaction through the UI
   - Refresh -- transaction persists
   - Navigate to all pages
   - Check http://localhost/actuator/health
5. `docker-compose down`
6. `docker-compose up` -- data should persist (volume)

**Acceptance Criteria:**
- [ ] All 3 containers start without errors
- [ ] UI is accessible on port 80
- [ ] API calls work through the nginx proxy
- [ ] Data persists after `docker-compose down` and `up` (volume)
- [ ] No CORS errors (nginx handles proxying)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

This ticket is a smoke test, not new code. Clean rebuild, full UI walk-through, then prove persistence: stop containers (but keep volumes), bring them back up, and confirm your data survived.

</details>

<details>
<summary><b>Hint 2 — Test script</b></summary>

```bash
# Clean slate
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
sleep 30

# Liveness
curl -s http://localhost/actuator/health | jq .   # {"status":"UP"}
curl -s http://localhost/api/transactions | jq 'length'

# Open UI, add a transaction manually, then:
curl -s http://localhost/api/transactions | jq 'length'   # +1 from before

# Persistence test
docker-compose down                # keeps the pgdata volume
docker-compose up -d
sleep 30
curl -s http://localhost/api/transactions | jq 'length'   # SAME as before
```

If the second count matches, the volume worked.

</details>

<details>
<summary><b>Hint 3 — Full end-to-end walk + cleanup</b></summary>

```bash
# 1. Clean start (-v drops the volume so we know we're at zero)
docker-compose down -v
docker volume prune -f       # extra paranoia

# 2. Fresh build (no cache so any Dockerfile change is picked up)
docker-compose build --no-cache

# 3. Boot
docker-compose up -d
docker-compose ps             # all 3 services Up

# 4. Wait for everything to be ready
until curl -fs http://localhost/actuator/health > /dev/null 2>&1; do
  echo "waiting..."; sleep 2
done
echo "App is up"

# 5. Smoke through every endpoint
curl -s http://localhost/api/transactions | jq 'length'   # 0 (clean DB w/ no seed)
curl -s http://localhost/api/categories   | jq 'length'   # whatever your seed creates
curl -s http://localhost/api/users        | jq 'length'

# 6. Add via UI:
open http://localhost
# Navigate to Add, submit a transaction, then:
curl -s http://localhost/api/transactions | jq 'length'   # 1

# 7. Persistence test
docker-compose down            # NOT -v
docker-compose up -d
sleep 20
curl -s http://localhost/api/transactions | jq 'length'   # still 1 ✓

# 8. Tear down fully when done
docker-compose down -v
```

**Common issues:**
- "permission denied: pgdata" on Linux → `sudo chown -R 999:999 ./pgdata` or use named volume (as in the compose file).
- Port 80 already in use → another web server is running on host. Stop it or change `"80:80"` → `"8081:80"`.
- Frontend loads but API returns 404 → nginx.conf `proxy_pass` target is wrong (should be `http://backend:8080` not `localhost`).
- CORS error → you're hitting `localhost:8080` directly from the React bundle. Make sure the frontend's fetch URLs use `/api/...` (relative) so nginx proxies them.

</details>

---

### TICKET-F113: GitHub Actions -- Build Job
**File:** `.github/workflows/ci.yml`

**Description:** Create a CI pipeline that builds the project on every push.

**What**
- `.github/workflows/ci.yml` with two parallel jobs — `build-backend` (setup-java, `mvn compile`) and `build-frontend` (setup-node, `npm ci`, `npm run build`) — triggered on push to main/develop and on PRs to main.

**Why**
- Local "works on my machine" is not proof.
- A clean Ubuntu runner builds from scratch on every push, so the moment someone breaks compilation it shows up as a red X on the commit — not three days later when the next dev tries to clone.

**Observe**
- GitHub → Actions tab shows the two jobs running in parallel after a push, each ~1-2 minutes; a green checkmark appears next to the commit hash.
- The workflow run page lists both jobs as successful.

**Instructions (follow the TODOs in ci.yml):**

```yaml
name: SmartBudget CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 25
      - name: Build backend
        run: cd backend && mvn compile

  build-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 22
      - name: Install dependencies
        run: cd frontend && npm ci
      - name: Build frontend
        run: cd frontend && npm run build
```

**Acceptance Criteria:**
- [ ] Pipeline triggers on push to main/develop and on PRs
- [ ] Backend build step compiles Java code
- [ ] Frontend build step installs and builds React
- [ ] Green checkmark appears on the commit in GitHub

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

GitHub Actions = YAML in `.github/workflows/*.yml`. Each workflow has `on:` (triggers), `jobs:` (parallel groups), each job has `steps:`. Use the official `actions/setup-java` and `actions/setup-node` actions — they handle versioning and caching.

</details>

<details>
<summary><b>Hint 2 — ci.yml</b></summary>

```yaml
# .github/workflows/ci.yml
name: SmartBudget CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 25
          cache: maven
      - name: Build backend
        run: cd backend && ./mvnw -B compile

  build-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: 22
          cache: npm
          cache-dependency-path: frontend/package-lock.json
      - run: cd frontend && npm ci
      - run: cd frontend && npm run build
```

Push to main → go to GitHub → Actions tab → see the two jobs run in parallel.

</details>

<details>
<summary><b>Hint 3 — Full ci.yml with caching</b></summary>

```yaml
# .github/workflows/ci.yml
name: SmartBudget CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build-backend:
    name: Backend (compile)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 25
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 25
          cache: maven                    # auto-caches ~/.m2

      - name: Maven compile
        working-directory: backend
        run: ./mvnw -B -DskipTests compile

  build-frontend:
    name: Frontend (build)
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up Node 22
        uses: actions/setup-node@v4
        with:
          node-version: 22
          cache: npm
          cache-dependency-path: frontend/package-lock.json

      - name: Install
        working-directory: frontend
        run: npm ci

      - name: Build
        working-directory: frontend
        run: npm run build
```

Verify:
1. Commit + push to a branch.
2. Open the repo on GitHub → **Actions** tab.
3. Click the latest run — you should see two jobs, each ~1-2 min, both green.
4. Add the badge to README:
```md
![CI](https://github.com/<your-org>/<repo>/actions/workflows/ci.yml/badge.svg)
```

Why `actions/setup-java@v4` with `cache: maven`? On a cold run, dependency download takes ~90s. On a cached run, it's ~5s. Same for `npm ci` with `cache: npm`.

</details>

---

### TICKET-F114: GitHub Actions -- Test Job
**File:** `.github/workflows/ci.yml`

**Description:** Add test steps to the CI pipeline.

**What**
- A `./mvnw test` step in `build-backend` (and optionally `npm test -- --run` in `build-frontend`) so CI runs the JUnit/Mockito/MockMvc suites you wrote on Days 6-8 on every push.

**Why**
- Compiling proves syntax; tests prove behaviour. Without this step a logic regression sails through CI green and lands in main.
- With it, a failing assertion exits non-zero, the workflow goes red, and the PR can't be merged.

**Observe**
- Workflow log shows `[INFO] Tests run: N, Failures: 0, Errors: 0`.
- On a deliberate break, the same log prints `[ERROR] Failures:` followed by the failing test name and the workflow ends with `BUILD FAILURE`.

**Instructions:**
1. Add `mvn test` step to the backend job
2. Add `npm test` step to the frontend job (if tests exist)
3. Tests run after build steps

**Acceptance Criteria:**
- [ ] Backend tests run and pass in CI
- [ ] Test results are visible in the GitHub Actions logs
- [ ] Failing tests cause the pipeline to fail (red X)

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Add `./mvnw test` step after the compile step in `build-backend`. If your React project has tests, add an `npm test -- --run` (Vitest non-watch mode) step in `build-frontend`. Failing tests exit non-zero, which marks the workflow red automatically.

</details>

<details>
<summary><b>Hint 2 — Updated jobs</b></summary>

```yaml
jobs:
  build-backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 25, cache: maven }
      - run: cd backend && ./mvnw -B test           # runs compile + test

  build-frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with: { node-version: 22, cache: npm, cache-dependency-path: frontend/package-lock.json }
      - run: cd frontend && npm ci
      - run: cd frontend && npm test -- --run        # if Vitest is set up
      - run: cd frontend && npm run build
```

`mvn test` will fail the step if any JUnit assertion fails, which fails the workflow.

</details>

<details>
<summary><b>Hint 3 — Full job + surface test results</b></summary>

```yaml
build-backend:
  name: Backend (test)
  runs-on: ubuntu-latest
  steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-java@v4
      with:
        distribution: temurin
        java-version: 25
        cache: maven

    - name: Run tests
      working-directory: backend
      run: ./mvnw -B test

    # Optional: surface Surefire reports nicely in the GitHub UI
    - name: Publish JUnit report
      if: always()                            # run even when tests fail
      uses: mikepenz/action-junit-report@v4
      with:
        report_paths: 'backend/target/surefire-reports/TEST-*.xml'

build-frontend:
  name: Frontend (test + build)
  runs-on: ubuntu-latest
  steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-node@v4
      with:
        node-version: 22
        cache: npm
        cache-dependency-path: frontend/package-lock.json

    - name: Install
      working-directory: frontend
      run: npm ci

    - name: Test
      working-directory: frontend
      run: npm test -- --run            # only if Vitest configured
      continue-on-error: false

    - name: Build
      working-directory: frontend
      run: npm run build
```

A failing JUnit test exits the JVM non-zero → `mvnw test` exits non-zero → GitHub step fails → workflow goes red → commit shows a red X.

If you don't have frontend tests yet, just delete that step. F116 has you intentionally break a test to see the red-X path.

</details>

---

### TICKET-F115: GitHub Actions -- Docker Build Job
**File:** `.github/workflows/ci.yml`

**Description:** Add a job that builds Docker images in CI.

**What**
- A third job `docker-build` with `needs: [build-backend, build-frontend]` that runs `docker compose build` to verify both Dockerfiles still produce valid images.

**Why**
- A typo in a Dockerfile (`COPY --from=biuld`, wrong base tag) doesn't show up in `mvn compile` or `npm run build` — it only surfaces when you actually build the image.
- CI catches that the moment it happens.

**Observe**
- GitHub Actions shows three jobs in the run graph; `docker-build` waits until the two build jobs go green, then runs and prints layer-by-layer build output for both images.
- A broken Dockerfile fails this job with a clear `failed to compute cache key` or `unknown instruction` error.

**Instructions:**
1. Add a new job `docker-build` that depends on build-backend and build-frontend
2. Build both Docker images:
   ```yaml
   - name: Build Docker images
     run: docker-compose build
   ```
3. This verifies that Dockerfiles are valid

**Acceptance Criteria:**
- [ ] Docker images build successfully in CI
- [ ] Job depends on the build jobs (runs after them)
- [ ] Build failure produces a clear error message

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Add a third job `docker-build` with `needs: [build-backend, build-frontend]`. Inside it, just run `docker-compose build`. The Ubuntu GitHub runner has Docker preinstalled, so no setup steps needed.

</details>

<details>
<summary><b>Hint 2 — Job</b></summary>

```yaml
docker-build:
  name: Docker images
  runs-on: ubuntu-latest
  needs: [build-backend, build-frontend]
  steps:
    - uses: actions/checkout@v4
    - name: Build images
      run: docker compose build
```

`docker compose` (with a space) is the v2 plugin, present on all current GitHub runners. `docker-compose` (hyphenated) also works on most images.

</details>

<details>
<summary><b>Hint 3 — Job with Buildx + cache</b></summary>

```yaml
docker-build:
  name: Docker images
  runs-on: ubuntu-latest
  needs: [build-backend, build-frontend]
  steps:
    - uses: actions/checkout@v4

    - name: Set up Docker Buildx
      uses: docker/setup-buildx-action@v3

    - name: Build backend image
      uses: docker/build-push-action@v5
      with:
        context: ./backend
        push:    false
        tags:    smartbudget-backend:ci
        cache-from: type=gha
        cache-to:   type=gha,mode=max

    - name: Build frontend image
      uses: docker/build-push-action@v5
      with:
        context: ./frontend
        push:    false
        tags:    smartbudget-frontend:ci
        cache-from: type=gha
        cache-to:   type=gha,mode=max
```

`needs:` makes this job wait for the two build jobs to succeed. If a Dockerfile syntax error sneaks in, the workflow goes red and the commit gets a clear failure on the docker-build job.

For Day 10 you can keep the simpler `docker compose build` version — Buildx + cache becomes useful when image builds get slow (~5+ minutes).

</details>

---

### TICKET-F116: Exercise -- Break a Test
**File:** N/A

**Description:** Intentionally break a test to see the CI pipeline fail.

**What**
- A deliberate one-line change to an existing assertion, pushed on a throwaway branch, then reverted — done so you've personally seen the red-X path end to end.

**Why**
- You can't trust a safety net you've never seen catch you.
- Watching CI go red on a real broken commit (and then green again after the fix) is the only way to know the test job actually blocks bad code.

**Observe**
- GitHub shows a red X on the chore branch's commit; the failing job log contains an `[ERROR] Failures:` block naming the broken test (e.g. `TransactionServiceTest.delete_existingItem_removesIt`).
- After the fix is pushed, the next run goes green within a few minutes.

**Instructions:**
1. Change an assertion in one of your test files to make it fail
2. Push the change to a branch
3. Watch the GitHub Actions pipeline -- it should show a red X
4. Read the error log to identify the failing test
5. Fix the test and push again -- pipeline should turn green

**Acceptance Criteria:**
- [ ] You have seen a red X on a GitHub Actions run
- [ ] You can read the logs to find which test failed
- [ ] You fixed the test and the pipeline turned green

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Pick a test and break an assertion (e.g., change `assertEquals(2, ...)` to `assertEquals(99, ...)`). Push it on a feature branch (don't break main). Watch CI fail. Read the log to find which test failed. Revert / fix → push again → green.

</details>

<details>
<summary><b>Hint 2 — Suggested break</b></summary>

In `TransactionServiceTest.java`, the F042 test:

```java
@Test
void delete_existingItem_removesIt() {
    svc.addTransaction(income);
    svc.delete(String.valueOf(income.getTxnId()));
    assertEquals(0, svc.size());     // ← change 0 to 1 to force failure
}
```

```bash
git checkout -b break-test
# edit the file
git add backend/src/test/java/...TransactionServiceTest.java
git commit -m "intentionally break a test (will revert)"
git push -u origin break-test
```

In GitHub → Actions → newest run is RED on `build-backend`. Click → expand the failing step → Maven prints:

```
[ERROR] Failures:
[ERROR]   TransactionServiceTest.delete_existingItem_removesIt:42
expected: <1> but was: <0>
[ERROR] Tests run: 6, Failures: 1
[ERROR] BUILD FAILURE
```

</details>

<details>
<summary><b>Hint 3 — Full break + fix workflow</b></summary>

```bash
# 1. Branch
git checkout -b chore/break-test

# 2. Make a deliberate failure
sed -i.bak 's/assertEquals(0, svc.size())/assertEquals(1, svc.size())/' \
    backend/src/test/java/com/smartbudget/service/TransactionServiceTest.java

# 3. Commit + push
git add -A
git commit -m "DELIBERATELY break delete test to verify CI catches it"
git push -u origin chore/break-test
```

Watch GitHub → Actions:
- ❌ `build-backend (test)` step fails on `TransactionServiceTest.delete_existingItem_removesIt`.
- Workflow status = RED.
- Commit on `chore/break-test` shows a red X.

Locate the failure in logs:
1. Click the failed run.
2. Click the failed job.
3. Expand the failing step.
4. Scroll to the `[ERROR] Failures:` block.

Fix:
```bash
# 4. Revert the assertion
sed -i.bak 's/assertEquals(1, svc.size())/assertEquals(0, svc.size())/' \
    backend/src/test/java/com/smartbudget/service/TransactionServiceTest.java
git commit -am "fix: restore correct assertion"
git push
```

Workflow goes green; commit shows ✓. **Don't merge the chore branch** — delete it:

```bash
git checkout main
git branch -D chore/break-test
git push origin --delete chore/break-test
```

This whole exercise proves the safety net works — a broken assertion stops a PR from merging.

</details>

---

### TICKET-F117: Production Properties
**File:** `backend/src/main/resources/application-prod.properties`

**Description:** Review the production configuration for PostgreSQL.

**What**
- A mental model of how `application.properties` (H2 dev defaults) and `application-prod.properties` (PostgreSQL overrides) compose when `SPRING_PROFILES_ACTIVE=prod` is set by docker-compose.

**Why**
- Shipping `ddl-auto=create-drop` or `h2.console.enabled=true` to production wipes user data or exposes a remote SQL shell.
- Profiles let one codebase serve both dev and prod safely — but only if you understand which file wins and why.

**Observe**
- You can name the four precedence layers in order (base properties → profile properties → env vars → CLI args) and explain why `ddl-auto` is `create-drop` in dev but `update` in prod without reading the file.

**Instructions:**
1. Open `application-prod.properties`
2. Understand how it differs from the H2 development config:
   - Uses PostgreSQL driver instead of H2
   - `ddl-auto=update` instead of `create-drop`
   - `sql.init.mode=never` (no seed data in production)
3. Understand that `SPRING_PROFILES_ACTIVE=prod` activates this file

**Acceptance Criteria:**
- [ ] You can explain the difference between dev (H2) and prod (PostgreSQL) configs
- [ ] You know that environment variables override properties
- [ ] You understand Spring profiles

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

`application.properties` = defaults (H2 dev). `application-prod.properties` = overrides activated when `spring.profiles.active=prod`. Docker-compose sets that env var. Read both files; understand which keys differ and why.

</details>

<details>
<summary><b>Hint 2 — Side-by-side</b></summary>

`application.properties` (dev — H2, in-memory):
```properties
spring.datasource.url=jdbc:h2:mem:smartbudget
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
spring.sql.init.mode=always
```

`application-prod.properties` (prod — PostgreSQL, persistent):
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update          # don't drop tables on restart!
spring.sql.init.mode=never                    # don't reseed
spring.h2.console.enabled=false               # not relevant in prod
spring.jpa.show-sql=false                     # don't log SQL in prod
```

Activate with `SPRING_PROFILES_ACTIVE=prod` env var (set in `docker-compose.yml`).

</details>

<details>
<summary><b>Hint 3 — Full explainer</b></summary>

The precedence rules Spring applies (later beats earlier):

1. `application.properties` (always loaded)
2. `application-{profile}.properties` if that profile is active (here: `prod`)
3. OS environment variables (`SPRING_DATASOURCE_URL` → `spring.datasource.url`)
4. Command-line args (`--server.port=9090`)

So in prod:
- Base file says H2 → overridden by `application-prod.properties` → values that read `${SPRING_DATASOURCE_URL}` → resolved from env → the compose-injected PostgreSQL URL.

Critical differences and why:

| Setting | Dev | Prod | Why |
|---|---|---|---|
| `ddl-auto` | `create-drop` | `update` | Dev wants a clean slate per run; prod must never drop data. |
| `sql.init.mode` | `always` | `never` | Dev wants seed data; prod has real data. |
| `h2.console.enabled` | `true` | `false` | Console is a giant security hole on a public host. |
| `show-sql` | `true` | `false` | Useful for learning; in prod it bloats logs and leaks data. |

Spring Profiles let you keep one codebase serve dev, staging, prod, e2e tests, etc. — by changing one env var instead of swapping files.

</details>

---

### TICKET-F118: Documentation Review
**File:** `README.md`

**Description:** Review and update the project README.

**What**
- A pass through the top-level `README.md` to verify every documented command, endpoint, and setup step actually works against the current code — plus a new Docker deployment section and a CI status badge.

**Why**
- README is the first thing a new dev (or interviewer) reads.
- A README with a broken `curl` example or a missing `docker-compose` section says "this project is half-finished" louder than any code.

**Observe**
- Every command in the Quick Start block runs successfully from a fresh clone; every endpoint in the API table responds with the documented status code.
- The CI badge at the top of the README renders green.

**Instructions:**
1. Verify all endpoints in the API Reference are correct
2. Add Docker deployment instructions
3. Add CI/CD badge configuration
4. Ensure the quick start guide works

**Acceptance Criteria:**
- [ ] README accurately reflects the current state of the project
- [ ] Docker deployment steps are documented
- [ ] API endpoints match the actual implementation

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Open `README.md`. Walk through every section: project overview, prerequisites, quick start, API table, Docker deployment, CI badge. Cross-check against the actual code — does the curl command actually work? Does the API table match what the controllers expose?

</details>

<details>
<summary><b>Hint 2 — Sections to verify</b></summary>

- **Quick Start** — does `./mvnw spring-boot:run` actually boot? Try it.
- **API Reference** — for each endpoint, run a curl and confirm the status/body match what's documented.
- **Docker Deployment** — does `docker-compose up` work from a fresh clone? Document `docker-compose down -v` for reset.
- **CI badge** — make sure the URL points to your actual repo, not a placeholder.

Suggested additions:
```md
## Docker Deployment

```bash
docker-compose build
docker-compose up
# UI: http://localhost
# API: http://localhost/api/...
# Health: http://localhost/actuator/health
docker-compose down       # stop (keeps data)
docker-compose down -v    # stop and wipe DB
```

![CI](https://github.com/<org>/<repo>/actions/workflows/ci.yml/badge.svg)
```

</details>

<details>
<summary><b>Hint 3 — Full README check + suggested additions</b></summary>

Use this checklist:

- [ ] **Title + tagline** — accurate one-liner
- [ ] **Badges** — CI, license, version (optional)
- [ ] **Architecture diagram** — text or PNG showing layers
- [ ] **Prerequisites** — Java 25, Node 22, Docker, Postgres
- [ ] **Quick start (dev)** — copy/pasteable commands
  ```bash
  cd backend  && ./mvnw spring-boot:run
  cd frontend && npm install && npm run dev
  ```
- [ ] **Quick start (Docker)** — `docker-compose up`
- [ ] **API Reference** — table with Method, URL, Body, Response codes
- [ ] **Project structure** — top-level folders explained
- [ ] **Testing** — `./mvnw test`, `npm test`
- [ ] **Deployment** — what env vars to set in prod
- [ ] **Tech stack** — exact versions
- [ ] **Contributing** — branch naming, PR template (optional)

Verification commands while you read:

```bash
# Each curl command in the README must actually work
docker-compose up -d
sleep 30
curl -i http://localhost/actuator/health    # 200
curl -s http://localhost/api/transactions | jq 'length > 0'
curl -s http://localhost/api/users          | jq 'length'

# Quick start commands
cd backend && ./mvnw -B compile             # passes
cd ../frontend && npm ci && npm run build   # passes
```

Anything documented that doesn't work → fix the code OR fix the docs (whichever is canonical). Don't ship a README with broken commands.

</details>

---

### TICKET-F119: Final Demo
**File:** N/A

**Description:** Prepare and deliver a final demo of SmartBudget.

**What**
- A rehearsed 10-minute live demo covering boot, CRUD across all three pages, savings goal contribution, the green CI run, and a quick code walkthrough — one speaking slot per team member.

**Why**
- This is the only artefact the room actually sees. A polished demo of a small feature set lands better than a rushed tour of everything.
- Rehearsal kills the dead air that kills credibility.

**Observe**
- The full demo lands inside 10 minutes (timer visible); every CRUD operation works live on the projector.
- The GitHub Actions tab shows a green check on the latest commit; every team member spoke at least once.

**Instructions:**
1. Start the app with `docker-compose up`
2. Demo the following:
   - Dashboard with real statistics and chart
   - Transaction list with filters
   - Adding a new transaction
   - Editing a transaction
   - Deleting a transaction
   - Savings goals with progress and contribute
   - Show the GitHub Actions pipeline
3. Be prepared to explain:
   - The 3-layer architecture (Controller -> Service -> Repository)
   - How Docker multi-stage builds work
   - How nginx proxies API requests
   - Why PreparedStatement prevents SQL injection

**Acceptance Criteria:**
- [ ] App runs fully in Docker containers
- [ ] All CRUD operations work in the demo
- [ ] Charts and filters work
- [ ] GitHub Actions pipeline is green
- [ ] You can explain the architecture end-to-end

<details>
<summary><b>Hint 1 — Quick nudge</b></summary>

Rehearse the demo the day before. Time it. Plan an exact 10-minute path: intro → live demo → code walkthrough → Q&A. Have screenshots ready as a fallback in case docker-compose chokes during the live demo. Every team member speaks at least one section.

</details>

<details>
<summary><b>Hint 2 — 10-minute script</b></summary>

| Time | What you do |
|---|---|
| 0:00 | "SmartBudget tracks income, expenses, savings goals — built across 10 days covering SQL, Java, Spring Boot, React, Docker, and CI/CD." |
| 0:30 | Show architecture diagram (Browser → nginx → Spring Boot → PostgreSQL). |
| 1:30 | `docker-compose up` — point out 3 containers starting. |
| 2:30 | Open `http://localhost`. Add a transaction. Show it appear. |
| 3:30 | Dashboard — bars updated. |
| 4:30 | Filter / edit / delete on Transaction list. |
| 5:30 | Contribute to a savings goal — bar moves. |
| 6:30 | Show GitHub Actions latest run (green ✓). |
| 7:00 | Code walkthrough: 1 file per layer (queries.sql → TransactionService.java → TransactionList.jsx → docker-compose.yml). |
| 9:00 | Q&A. |
| 9:45 | Retrospective: each member says one thing they learned. |

</details>

<details>
<summary><b>Hint 3 — Full pre-demo checklist + Q&A prep</b></summary>

**Day-before checklist:**
- [ ] `git pull` on demo laptop.
- [ ] `docker-compose down -v && docker-compose build --no-cache && docker-compose up -d` — boots clean.
- [ ] Reset DB to a known good state (a curated set of seed transactions).
- [ ] Force a clean commit + push so the latest GitHub Actions run is green.
- [ ] Take screenshots of every demo step (fallback if live demo dies).
- [ ] Test the actual demo laptop on the actual projector — fonts/colours can shift.
- [ ] Have a timer visible.

**Each member speaks at least once.** Suggested split:
- Member A: Architecture intro + docker-compose boot.
- Member B: UI demo (add / list / edit / delete).
- Member C: Savings goal contribution + chart + GitHub Actions.
- Member D: Code walkthrough.
- Everyone: Q&A.

**Anticipate Q&A:**

| Question | Short answer |
|---|---|
| "Why JPA instead of raw JDBC?" | Less boilerplate; safer; auto-generated SQL. Trade-off: harder for complex analytics. |
| "How does Docker know `backend` means the Spring container?" | docker-compose creates a private network where service names resolve as hostnames. |
| "What stops SQL injection?" | PreparedStatement (JDBC) and parameterized JPA queries — values sent separately from SQL. |
| "Why nginx in front of React?" | Serves static `dist/` files efficiently AND proxies `/api/*` to the backend so the frontend doesn't need CORS. |
| "How would you scale this?" | Horizontally: multiple backend containers behind a load balancer; PostgreSQL becomes a managed service (RDS/Cloud SQL); add Redis cache. |

**Plan B for live demo failure:**
- Screenshots ready.
- Pre-recorded 30-second screencast of the happy path.
- Don't panic — say "let me show you the screenshots while we sort this out" and continue.

</details>

---

## End-of-Day Checklist

- [ ] Backend Dockerfile with multi-stage build works
- [ ] Frontend Dockerfile with nginx works
- [ ] nginx.conf proxies /api/ and serves SPA correctly
- [ ] docker-compose starts all 3 services
- [ ] Health checks ensure proper startup order
- [ ] GitHub Actions pipeline builds, tests, and Docker-builds
- [ ] Final demo delivered successfully
- [ ] You can explain: Docker multi-stage builds, nginx reverse proxy, docker-compose networking, CI/CD pipelines, Spring profiles

---

## Congratulations!

You have built a full-stack financial application from scratch:
- Database design (SQL)
- Backend API (Java, Spring Boot, JPA)
- Frontend UI (React, Recharts)
- Testing (JUnit, Mockito, MockMvc)
- Containerization (Docker, nginx)
- CI/CD (GitHub Actions)

This is the same technology stack used in production at financial institutions.

---

*SmartBudget -- Deutsche Bank | TDI 2026 Foundation Track*
