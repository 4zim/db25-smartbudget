# ============================================================
# TICKET-F109 (Day 10, Sprint 9) — Frontend Dockerfile (Multi-Stage Build)
# ============================================================
#
# WHAT: Builds the React app and serves it with nginx.
#       Same multi-stage concept as the backend Dockerfile:
#         Stage 1: Uses Node.js to COMPILE React (JSX → plain JavaScript)
#         Stage 2: Uses nginx to SERVE the compiled static files
#       Node.js is NOT needed at runtime — only nginx.
#
# WHY:  React code (JSX, modern JavaScript, imports) can't run directly in browsers.
#       "npm run build" transforms it into plain HTML/CSS/JS that any browser understands.
#       In production, you need a web server (nginx) to deliver those files — not Node.js.
#       nginx is faster, uses less memory, and handles thousands of concurrent connections.
#
# ANALOGY:
#       Stage 1 is the bakery kitchen (oven, mixer, ingredients) → produces bread
#       Stage 2 is the shop counter (just a shelf) → serves the bread to customers
#       The kitchen equipment doesn't go to the customer.
#
# VISUAL: How the two stages work together:
#
#       ┌─── Stage 1: "builder" (Node.js) ─────────┐
#       │  node:22-alpine                           │
#       │  package.json → npm ci (install deps)     │
#       │  src/         → npm run build             │
#       │  OUTPUT: /app/dist/                       │
#       │    ├── index.html                         │
#       │    └── assets/                            │
#       │        ├── index-a1b2c3.js   (bundled JS) │
#       │        └── index-d4e5f6.css  (bundled CSS)│
#       └──────────────────────────┬────────────────┘
#                                  │ COPY --from=builder
#                                  ▼
#       ┌─── Stage 2: nginx ───────────────────────┐
#       │  nginx:alpine (~25MB total image)         │
#       │  /usr/share/nginx/html/ (built files)     │
#       │  /etc/nginx/conf.d/default.conf (config)  │
#       │  EXPOSE 80                                │
#       └───────────────────────────────────────────┘
#
# PREREQUISITES:
#   - Docker Desktop must be installed and running
#   - Run from the frontend/ directory: cd frontend && docker build -t smartbudget-frontend .
#   - nginx.conf must exist in the frontend/ directory (TICKET-F109)
#
# ============================================================


# -------------------------------------------------------
# TODO TICKET-F109: Stage 1 — Build stage (6 lines to write)
# -------------------------------------------------------
#
# WHAT: Uses Node.js to install dependencies and compile the React app.
#       "npm run build" does the compilation:
#         - JSX → JavaScript (browsers don't understand JSX)
#         - import/export → bundled into one file (fewer HTTP requests)
#         - CSS → minified (whitespace removed)
#         - Tree-shaking → unused code removed
#       The result: a small dist/ folder with optimized static files.
#
# HOW — Write these 6 lines in order:
#
#   LINE 1: FROM instruction — Node.js base image
#     ┌─────────────────────────────────────────────────────────┐
#     │ FROM node:22-alpine AS builder                          │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       node:22        = Official Node.js image (version 22 LTS)
#       alpine         = Minimal Linux base (~50MB instead of ~350MB for debian)
#       AS builder     = Name this stage so Stage 2 can COPY from it
#
#   LINE 2: WORKDIR
#     ┌─────────────────────────────────────────────────────────┐
#     │ WORKDIR /app                                            │
#     └─────────────────────────────────────────────────────────┘
#
#   LINE 3: COPY package files first (for dependency caching)
#     ┌─────────────────────────────────────────────────────────┐
#     │ COPY package*.json ./                                   │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       package*.json = Matches both package.json AND package-lock.json
#       The * is a wildcard: "any characters between 'package' and '.json'"
#       Copying these FIRST means npm ci is cached when only source code changes.
#
#   LINE 4: RUN — install dependencies
#     ┌─────────────────────────────────────────────────────────┐
#     │ RUN npm ci                                              │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       npm ci = "Clean Install" — installs EXACT versions from package-lock.json
#
#     npm ci vs npm install:
#       npm install → may update versions, modifies lock file → non-deterministic
#       npm ci      → exact versions, fails if lock file is outdated → deterministic
#       ALWAYS use npm ci in Dockerfiles and CI pipelines for reproducible builds.
#
#   LINE 5: COPY all source files
#     ┌─────────────────────────────────────────────────────────┐
#     │ COPY . .                                                │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       COPY . . = "Copy EVERYTHING from my laptop's current directory into /app/"
#       This includes: src/, public/, vite.config.js, index.html, etc.
#       NOTE: If you have a .dockerignore file, it excludes files listed there
#       (like node_modules/ — you don't want to copy the host's node_modules).
#
#   LINE 6: RUN — build the React app
#     ┌─────────────────────────────────────────────────────────┐
#     │ RUN npm run build                                       │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       npm run build = Runs the "build" script from package.json
#       For Vite: this runs "vite build" which creates /app/dist/
#       For CRA:  this runs "react-scripts build" which creates /app/build/
#       Our project uses Vite, so the output is in /app/dist/
#
# OBSERVE: After Stage 1 completes, look for this line in the build output:
#          "dist/index.html   0.xx kB"
#          "dist/assets/index-xxxxx.js   xxx kB"
#          This confirms the build succeeded and shows file sizes.
#
# CACHING VISUALIZATION:
#   ┌─────────────────────────────────────────────────────────┐
#   │  Layer 1: FROM node:22-alpine    → cached (never changes) │
#   │  Layer 2: COPY package*.json     → cached if deps unchanged│
#   │  Layer 3: RUN npm ci             → cached if deps unchanged│
#   │  Layer 4: COPY . .               → rebuilt on code changes │
#   │  Layer 5: RUN npm run build      → rebuilt on code changes │
#   └─────────────────────────────────────────────────────────┘


# -------------------------------------------------------
# TODO TICKET-F109: Stage 2 — Serve stage (4 lines to write)
# -------------------------------------------------------
#
# WHAT: A minimal nginx container that serves the compiled React files.
#       nginx (pronounced "engine-X") is a high-performance web server.
#       It serves static files and proxies API requests to the backend.
#
# HOW — Write these 4 lines in order:
#
#   LINE 1: FROM instruction — start fresh with nginx
#     ┌─────────────────────────────────────────────────────────┐
#     │ FROM nginx:alpine                                       │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       This starts a COMPLETELY NEW image. Stage 1 is discarded.
#       nginx:alpine = Official nginx image on minimal Alpine Linux (~25MB total)
#       No "AS" name needed — this is the final stage that ships.
#
#   LINE 2: COPY built files from Stage 1
#     ┌─────────────────────────────────────────────────────────┐
#     │ COPY --from=builder /app/dist /usr/share/nginx/html     │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       --from=builder           = Reach into the discarded Stage 1
#       /app/dist                = The folder containing built React files
#       /usr/share/nginx/html    = nginx's default directory for serving files
#       After this, nginx automatically serves index.html when you visit the site.
#
#     COMMON MISTAKE: Using /app/build instead of /app/dist
#       /app/build = Create React App (CRA) output directory
#       /app/dist  = Vite output directory  ← Our project uses Vite
#
#   LINE 3: COPY custom nginx configuration
#     ┌─────────────────────────────────────────────────────────┐
#     │ COPY nginx.conf /etc/nginx/conf.d/default.conf          │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       nginx.conf                        = YOUR custom config (from this directory)
#       /etc/nginx/conf.d/default.conf    = Replaces nginx's default config
#       This tells nginx how to handle /api/* requests (proxy to backend)
#       and how to handle React Router URLs (try_files fallback to index.html).
#
#     NOTE: This file MUST exist in the frontend/ directory before building!
#           If missing, the build fails with "COPY failed: file not found"
#
#   LINE 4: EXPOSE — document the port
#     ┌─────────────────────────────────────────────────────────┐
#     │ EXPOSE 80                                               │
#     └─────────────────────────────────────────────────────────┘
#     BREAKDOWN:
#       nginx listens on port 80 by default (standard HTTP port).
#       Remember: EXPOSE is documentation, not port opening.
#       You map it when running: docker run -p 3000:80 smartbudget-frontend
#       This means: host port 3000 → container port 80
#
# OBSERVE — Verify your work:
#   1. Build:  cd frontend && docker build -t smartbudget-frontend .
#      Watch both stages complete.
#
#   2. Check size:  docker images | grep smartbudget-frontend
#      Should be ~25-30MB! Compare with node:22-alpine which is ~180MB.
#      The multi-stage build discarded Node.js entirely.
#
#   3. Run:    docker run -p 3000:80 smartbudget-frontend
#      Visit http://localhost:3000 → should see the React app.
#      NOTE: API calls will fail because there's no backend running.
#      That's expected — use docker-compose to run everything together.
#
#   4. Inspect: docker exec -it <container_id> ls /usr/share/nginx/html
#      You should see: index.html, assets/ — just the built files.
#      No node_modules, no src/, no package.json — just the essentials.
#
# COMMON MISTAKES:
#   WRONG: Forgetting "AS builder" in Stage 1 → COPY --from=builder fails
#   WRONG: Using /app/build instead of /app/dist → empty nginx page (Vite uses dist/)
#   WRONG: Missing nginx.conf file → build fails with COPY error
#   WRONG: Not creating .dockerignore → node_modules from host gets copied (slow build)
#   WRONG: Using node:22 instead of node:22-alpine → Stage 1 is 350MB instead of 50MB
#
# BONUS — Create a .dockerignore file (optional but recommended):
#   Create a file called ".dockerignore" in the frontend/ directory with:
#     node_modules
#     dist
#     .git
#   This prevents copying the host's node_modules (which may be different
#   from the container's OS) and speeds up the COPY . . step.
