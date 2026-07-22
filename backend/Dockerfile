# ============================================================
# TICKET-F108 (Day 10, Sprint 9) — Backend Dockerfile (Multi-Stage Build)
# ============================================================
#
# WHAT: A Dockerfile is a recipe for building a container image.
#       Think of it like a cooking recipe:
#         - Each instruction (FROM, COPY, RUN) is a step
#         - Each step creates a "layer" (like layers of a cake)
#         - The final image is all layers stacked together
#
#       A "multi-stage build" uses TWO separate recipes in ONE file:
#         Stage 1 (builder): Has all the TOOLS (Maven, JDK) to compile code
#         Stage 2 (runtime): Has only what's needed to RUN the app (JRE + JAR)
#         The builder stage is thrown away — only the runtime stage ships.
#
# WHY:  Without multi-stage, your image includes Maven (~300MB) + source code
#       + JDK (~400MB) — stuff you don't need at runtime. Total: ~700MB.
#       With multi-stage, you only ship JRE (~150MB) + JAR (~50MB) = ~200MB.
#       Smaller images = faster deployments, less storage, less attack surface.
#
# ANALOGY:
#       Building a house: Stage 1 is the construction site (cranes, scaffolding,
#       cement mixers). Stage 2 is the finished house (just the building).
#       You don't ship the cranes to the homeowner.
#
# VISUAL: How the two stages work together:
#
#       ┌─── Stage 1: "builder" ───────────────────┐
#       │  Maven + JDK + source code                │
#       │  pom.xml → download dependencies          │
#       │  src/    → compile to JAR                  │
#       │  OUTPUT: target/smartbudget-0.0.1.jar      │
#       └──────────────────────────────┬─────────────┘
#                                      │ COPY --from=builder
#                                      ▼
#       ┌─── Stage 2: runtime ─────────────────────┐
#       │  JRE only (no Maven, no source code)      │
#       │  app.jar (copied from Stage 1)            │
#       │  ENTRYPOINT: java -jar app.jar            │
#       │  EXPOSE 8080                              │
#       └───────────────────────────────────────────┘
#
# PREREQUISITES:
#   - Docker Desktop must be installed and running
#   - Run this from the backend/ directory: cd backend && docker build -t smartbudget-backend .
#   - The "." at the end means "use the current directory as the build context"
#
# ============================================================


# -------------------------------------------------------
# TODO TICKET-F108: Stage 1 — Build stage (6 lines to write)
# -------------------------------------------------------
#
# WHAT: This stage uses Maven to compile your Spring Boot project into
#       an executable JAR file. The JAR contains:
#         - Your compiled .class files
#         - All dependencies (Spring, Hibernate, etc.)
#         - An embedded Tomcat web server
#       After this stage, everything you need is in ONE file: target/*.jar
#
# HOW — Write these 6 lines in order:
#
#   LINE 1: FROM instruction — choose the base image
#     ┌─────────────────────────────────────────────────────────┐
#     │ FROM maven:3.9-eclipse-temurin-25 AS builder            │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       FROM         = "Start with this pre-built image as the foundation"
#       maven:3.9    = Official Maven image (version 3.9) — includes Maven CLI
#       eclipse-temurin-25 = Includes JDK 25 (the Java compiler)
#       AS builder   = Names this stage "builder" so Stage 2 can reference it
#
#     COMMON MISTAKE: Forgetting "AS builder" → Stage 2 can't copy the JAR
#
#   LINE 2: WORKDIR instruction — set the working directory
#     ┌─────────────────────────────────────────────────────────┐
#     │ WORKDIR /app                                            │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       WORKDIR /app = "All subsequent commands run inside /app"
#       If /app doesn't exist, Docker creates it automatically.
#       Think of it like "cd /app" but permanent for all following steps.
#
#   LINE 3: COPY pom.xml first (for dependency caching)
#     ┌─────────────────────────────────────────────────────────┐
#     │ COPY pom.xml .                                          │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       COPY pom.xml .  = "Copy pom.xml from my laptop into /app/ in the image"
#       The "." means "current WORKDIR" which is /app
#
#     WHY COPY pom.xml FIRST? Docker caching optimization:
#       Docker caches each layer. If pom.xml hasn't changed since last build,
#       Docker skips re-downloading dependencies (Line 4). This saves 2-5 minutes.
#       If you COPY everything at once, ANY source code change re-downloads deps.
#
#   LINE 4: RUN — download all dependencies
#     ┌─────────────────────────────────────────────────────────┐
#     │ RUN mvn dependency:go-offline                           │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       RUN            = "Execute this shell command during the build"
#       mvn dependency:go-offline = "Download ALL dependencies listed in pom.xml"
#       This layer is CACHED if pom.xml hasn't changed — huge time savings!
#
#   LINE 5: COPY source code
#     ┌─────────────────────────────────────────────────────────┐
#     │ COPY src ./src                                          │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       COPY src ./src = "Copy the src/ directory from my laptop into /app/src/"
#       This is copied AFTER dependencies so that code changes don't bust the cache.
#
#   LINE 6: RUN — compile the project
#     ┌─────────────────────────────────────────────────────────┐
#     │ RUN mvn clean package -DskipTests                       │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       mvn clean      = "Delete previous build output"
#       package        = "Compile code + run tests + create JAR"
#       -DskipTests    = "Skip tests during image build (tests run in CI instead)"
#       OUTPUT: Creates target/smartbudget-0.0.1-SNAPSHOT.jar
#
# WHY THIS ORDER MATTERS (Docker layer caching):
#
#   ┌─────────────────────────────────────────────────────────┐
#   │  Layer 1: FROM maven         → cached (never changes)  │
#   │  Layer 2: COPY pom.xml       → cached if pom unchanged │
#   │  Layer 3: RUN mvn deps       → cached if pom unchanged │
#   │  Layer 4: COPY src           → rebuilt on code changes  │
#   │  Layer 5: RUN mvn package    → rebuilt on code changes  │
#   └─────────────────────────────────────────────────────────┘
#   Result: Only layers 4-5 rebuild when you change source code.
#   Without this optimization, ALL layers rebuild every time.
#
# OBSERVE: Run "docker build -t smartbudget-backend ." from the backend/ directory.
#          First build: slow (downloads dependencies). Watch "Downloading from central..."
#          Second build (no code changes): instant! Watch "CACHED" next to each step.
#          Third build (change a .java file): only layers 4-5 rebuild — deps stay cached.


# -------------------------------------------------------
# TODO TICKET-F108: Stage 2 — Runtime stage (5 lines to write)
# -------------------------------------------------------
#
# WHAT: A minimal container that ONLY runs the compiled JAR.
#       No Maven, no JDK, no source code — just the Java Runtime (JRE) + your JAR.
#       Uses "alpine" — a stripped-down Linux (~5MB) with only the essentials.
#
# HOW — Write these 5 lines in order:
#
#   LINE 1: FROM instruction — start a NEW, clean stage
#     ┌─────────────────────────────────────────────────────────┐
#     │ FROM eclipse-temurin:25-jre-alpine                      │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       FROM            = Start a completely fresh image (Stage 1 is discarded!)
#       eclipse-temurin = Official Java runtime from Adoptium
#       25-jre          = Java 25, JRE only (not JDK — no compiler needed at runtime)
#       alpine          = Minimal Linux base (~5MB instead of ~80MB for debian)
#
#     NOTE: No "AS" name here — this is the FINAL stage, it becomes the shipped image.
#
#   LINE 2: WORKDIR
#     ┌─────────────────────────────────────────────────────────┐
#     │ WORKDIR /app                                            │
#     └─────────────────────────────────────────────────────────┘
#
#   LINE 3: COPY the JAR from Stage 1
#     ┌─────────────────────────────────────────────────────────┐
#     │ COPY --from=builder /app/target/*.jar app.jar           │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       --from=builder  = "Copy from Stage 1 (named 'builder'), NOT from my laptop"
#       /app/target/*.jar = The JAR file that Maven created in Stage 1
#       app.jar         = Rename it to "app.jar" for simplicity
#
#     THIS IS THE MAGIC OF MULTI-STAGE BUILDS:
#       You reach BACK into the discarded Stage 1 and grab just the artifact you need.
#       Everything else (Maven, source code, .class files) is gone.
#
#   LINE 4: EXPOSE — document the port
#     ┌─────────────────────────────────────────────────────────┐
#     │ EXPOSE 8080                                             │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       EXPOSE 8080 = "This container listens on port 8080"
#       NOTE: EXPOSE is documentation only! It does NOT open the port.
#       You still need -p 8080:8080 when running the container.
#
#   LINE 5: ENTRYPOINT — the startup command
#     ┌─────────────────────────────────────────────────────────┐
#     │ ENTRYPOINT ["java", "-jar", "app.jar"]                  │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       ENTRYPOINT = "When the container starts, run this command"
#       ["java", "-jar", "app.jar"] = Runs: java -jar app.jar
#       The JSON array format (exec form) is preferred over string format.
#
#     OPTIONAL ENHANCEMENT — activate the production Spring profile:
#       ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
#       This tells Spring Boot to load application-prod.properties,
#       which reads database credentials from environment variables
#       instead of the hardcoded H2 settings in application.properties.
#
#     ENTRYPOINT vs CMD:
#       ENTRYPOINT = always runs, can't be overridden easily
#       CMD        = default command, can be overridden by docker run arguments
#       For apps, use ENTRYPOINT. For utility containers, use CMD.
#
# OBSERVE — Verify your work:
#   1. Build:  cd backend && docker build -t smartbudget-backend .
#      Watch the output — you should see both stages build.
#
#   2. Check size:  docker images | grep smartbudget-backend
#      Should be ~200MB (not ~500MB). Compare with: docker images | grep maven
#
#   3. Run:    docker run -p 8080:8080 smartbudget-backend
#      You should see the Spring Boot banner in your terminal.
#      Visit http://localhost:8080/api/categories → should return JSON.
#      Press Ctrl+C to stop the container.
#
#   4. Debug: If it fails to start, check the logs:
#      docker run smartbudget-backend (without -p) just to see error output.
#      Common error: "Cannot connect to database" — expected when running standalone
#      (no PostgreSQL container). This is fixed by docker-compose.
#
# COMMON MISTAKES:
#   WRONG: Forgetting "AS builder" in Stage 1 → Stage 2 can't find the JAR
#   WRONG: Using JDK instead of JRE in Stage 2 → image is 100MB+ larger for no reason
#   WRONG: Copying pom.xml and src/ together → dependency cache never works
#   WRONG: Using COPY instead of COPY --from=builder → copies from laptop, not Stage 1
#   WRONG: Forgetting WORKDIR → files end up in / (root) which is messy
