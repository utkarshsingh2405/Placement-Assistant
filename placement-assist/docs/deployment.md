# Institute Pillar Deployment & Operations Guide

This guide details the procedure for deploying, configuring, containerizing, and operating the Institute pillar of the Placement Assistance Web Application in staging and production environments.

---

## 1. Environment Variables Configuration

Create a `.env` file or export the following environment variables in your server environment:

| Variable | Default Value | Description |
| :--- | :--- | :--- |
| `PORT` | `8080` | HTTP port on which Spring Boot listens |
| `SPRING_PROFILES_ACTIVE` | `prod` | Active Spring profile (`dev` or `prod`) |
| `DATABASE_URL` | `jdbc:sqlite:institute.db` | JDBC connection string to SQLite database file |
| `JWT_SECRET` | `404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970` | 256-bit secret key for HMAC-SHA256 JWT signing |
| `JWT_ACCESS_EXPIRATION` | `86400000` | JWT Access token expiration time in milliseconds (24 hours) |
| `JWT_REFRESH_EXPIRATION`| `604800000` | JWT Refresh token expiration time in milliseconds (7 days) |
| `INSTITUTE_API_KEY` | `inst-sec-key-7f9a12b4e8c345d6` | Pre-shared key for placement pillar integration (`X-API-KEY`) |
| `FILE_STORAGE_LOCATION` | `uploads/` | Directory on disk where uploaded PDF resumes are stored |

---

## 2. Compiling the Production Binary

Build the executable fat JAR from the workspace root:

```bash
mvn clean package -DskipTests
```

The compiled JAR is located at:
```text
institute/target/institute-1.0.0-SNAPSHOT.jar
```

---

## 3. Database Migrations (Flyway)

Flyway automatically applies SQL migrations on startup from:
```text
institute/src/main/resources/db/migration/V1__init.sql
```
- Flyway tracks executed migrations in the table `flyway_schema_history`.
- In production, Flyway verifies checksums before starting the application. Do not modify already-applied migration files.

---

## 4. Running as a Systemd Service (Linux)

To run the application as a background service managed by `systemd`:

1. Copy the JAR to `/opt/placement-assist/institute.jar`.
2. Create the configuration file `/etc/systemd/system/placement-institute.service`:

```ini
[Unit]
Description=Placement Assist Institute Service
After=network.target

[Service]
User=placement
Group=placement
WorkingDirectory=/opt/placement-assist
EnvironmentFile=/opt/placement-assist/.env
ExecStart=/usr/bin/java -Xms256m -Xmx1024m -jar /opt/placement-assist/institute.jar
SuccessExitStatus=143
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

3. Enable and start the service:
```bash
sudo systemctl daemon-reload
sudo systemctl enable placement-institute
sudo systemctl start placement-institute
sudo systemctl status placement-institute
```

---

## 5. Docker Deployment

### 5.1 Multi-Stage Container Build
The included `institute/Dockerfile` performs a clean Maven compile in an Eclipse Temurin build image, then copies the standalone JAR into an optimized JRE 17 runtime container:

```bash
docker build -t placement-assist-institute:latest -f institute/Dockerfile .
```

### 5.2 Running via Docker Compose
Run the pre-configured compose stack:

```bash
docker compose up -d --build
```

To view logs:
```bash
docker compose logs -f
```

---

## 6. Nginx Reverse Proxy Configuration

When exposing the application to public internet traffic, configure Nginx as a reverse proxy with SSL termination and payload size configuration for PDF uploads:

```nginx
server {
    listen 80;
    server_name placements.yourcollege.edu;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2;
    server_name placements.yourcollege.edu;

    ssl_certificate /etc/letsencrypt/live/placements.yourcollege.edu/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/placements.yourcollege.edu/privkey.pem;

    # Allow PDF resume uploads up to 20MB
    client_max_body_size 20M;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_cache_bypass $http_upgrade;
    }
}
```

---

## 7. SQLite Backup & Maintenance

Because SQLite is running in WAL (`Write-Ahead Logging`) mode, online backups can be executed without downtime using the SQLite Online Backup API or CLI:

```bash
# Safely snapshot the running database into a backup file:
sqlite3 institute.db ".backup 'backups/institute_$(date +%Y%m%d_%H%M%S).db'"
```

Set up a daily cron job to back up the database and upload snapshots to secure remote object storage (AWS S3, Google Cloud Storage, or Azure Blob).
