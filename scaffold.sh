#!/usr/bin/env bash
set -e

echo "Scaffolding Placement Assistance (Java Spring Boot + HTML/CSS/JS) Institute Pillar..."

# Clean previous placement-assist directory if needed
rm -rf placement-assist

# Root & Docs directories
mkdir -p placement-assist/docs/prompts

# Shared-lib directories
mkdir -p placement-assist/shared-lib/src/main/java/com/placementassist/shared/security \
         placement-assist/shared-lib/src/main/java/com/placementassist/shared/api \
         placement-assist/shared-lib/src/main/java/com/placementassist/shared/exception \
         placement-assist/shared-lib/src/main/java/com/placementassist/shared/util \
         placement-assist/shared-lib/src/main/java/com/placementassist/shared/config

# Shared-web directories
mkdir -p placement-assist/shared-web/css \
         placement-assist/shared-web/js

# Institute backend module directories
mkdir -p placement-assist/institute/src/main/java/com/placementassist/institute/config \
         placement-assist/institute/src/main/java/com/placementassist/institute/common/enums \
         placement-assist/institute/src/main/java/com/placementassist/institute/jobs \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/auth/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/student/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/companydrive/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/questionbank/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/attendance/crt/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/attendance/classroom/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/driveround/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/resume/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/hackathon/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/internship/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/offcampusjob/dto \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/listing \
         placement-assist/institute/src/main/java/com/placementassist/institute/modules/dashboard/dto

# Institute resources & static web directories
mkdir -p placement-assist/institute/src/main/resources/db/migration \
         placement-assist/institute/src/main/resources/static/css \
         placement-assist/institute/src/main/resources/static/js \
         placement-assist/institute/src/main/resources/static/assets \
         placement-assist/institute/src/main/resources/static/pages/1-students \
         placement-assist/institute/src/main/resources/static/pages/2-company-drives \
         placement-assist/institute/src/main/resources/static/pages/3-question-bank \
         placement-assist/institute/src/main/resources/static/pages/4-attendance/crt \
         placement-assist/institute/src/main/resources/static/pages/4-attendance/class \
         placement-assist/institute/src/main/resources/static/pages/5-drive-rounds \
         placement-assist/institute/src/main/resources/static/pages/6-resume-ats \
         placement-assist/institute/src/main/resources/static/pages/7-hackathons \
         placement-assist/institute/src/main/resources/static/pages/8-internships \
         placement-assist/institute/src/main/resources/static/pages/9-offcampus-jobs \
         placement-assist/institute/src/test/java/com/placementassist/institute

# Root files
touch placement-assist/README.md \
      placement-assist/pom.xml \
      placement-assist/docker-compose.yml \
      placement-assist/.gitignore \
      placement-assist/.env.example

# Shared-lib files
touch placement-assist/shared-lib/pom.xml \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/security/JwtService.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/security/JwtAuthFilter.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/security/Role.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/security/SecurityUtils.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/api/ApiResponse.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/api/PageResponse.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/api/ErrorResponse.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/exception/ApiException.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/exception/NotFoundException.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/exception/ConflictException.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/exception/GlobalExceptionHandler.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/util/CsvUtil.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/util/DateUtil.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/util/SpecificationBuilder.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/config/OpenApiConfig.java \
      placement-assist/shared-lib/src/main/java/com/placementassist/shared/config/CorsConfig.java

# Shared-web files
touch placement-assist/shared-web/css/theme.css \
      placement-assist/shared-web/css/components.css \
      placement-assist/shared-web/js/api.js \
      placement-assist/shared-web/js/auth.js \
      placement-assist/shared-web/js/ui.js \
      placement-assist/shared-web/js/table.js \
      placement-assist/shared-web/js/pagination.js \
      placement-assist/shared-web/js/charts.js

# Institute files
touch placement-assist/institute/pom.xml \
      placement-assist/institute/Dockerfile \
      placement-assist/institute/src/main/java/com/placementassist/institute/InstituteApplication.java \
      placement-assist/institute/src/main/java/com/placementassist/institute/config/SecurityConfig.java \
      placement-assist/institute/src/main/java/com/placementassist/institute/config/SchedulingConfig.java \
      placement-assist/institute/src/main/java/com/placementassist/institute/config/FileStorageConfig.java \
      placement-assist/institute/src/main/java/com/placementassist/institute/common/BaseEntity.java \
      placement-assist/institute/src/main/java/com/placementassist/institute/jobs/ExpireListingsJob.java \
      placement-assist/institute/src/main/java/com/placementassist/institute/jobs/RemindersJob.java

# Institute Resources & Migrations
touch placement-assist/institute/src/main/resources/application.yml \
      placement-assist/institute/src/main/resources/application-dev.yml \
      placement-assist/institute/src/main/resources/db/migration/V1__init.sql

# Institute Static Pages
touch placement-assist/institute/src/main/resources/static/index.html \
      placement-assist/institute/src/main/resources/static/login.html \
      placement-assist/institute/src/main/resources/static/register.html \
      placement-assist/institute/src/main/resources/static/dashboard.html \
      placement-assist/institute/src/main/resources/static/css/.gitkeep \
      placement-assist/institute/src/main/resources/static/js/.gitkeep \
      placement-assist/institute/src/main/resources/static/assets/.gitkeep \
      placement-assist/institute/src/main/resources/static/pages/1-students/student-list.html \
      placement-assist/institute/src/main/resources/static/pages/1-students/student-form.html \
      placement-assist/institute/src/main/resources/static/pages/1-students/student-profile.html \
      placement-assist/institute/src/main/resources/static/pages/2-company-drives/drive-list.html \
      placement-assist/institute/src/main/resources/static/pages/2-company-drives/drive-form.html \
      placement-assist/institute/src/main/resources/static/pages/2-company-drives/drive-detail.html \
      placement-assist/institute/src/main/resources/static/pages/2-company-drives/company-detail.html \
      placement-assist/institute/src/main/resources/static/pages/3-question-bank/question-list.html \
      placement-assist/institute/src/main/resources/static/pages/3-question-bank/question-form.html \
      placement-assist/institute/src/main/resources/static/pages/3-question-bank/practice-mode.html \
      placement-assist/institute/src/main/resources/static/pages/4-attendance/crt/crt-sessions.html \
      placement-assist/institute/src/main/resources/static/pages/4-attendance/crt/mark-crt.html \
      placement-assist/institute/src/main/resources/static/pages/4-attendance/crt/crt-report.html \
      placement-assist/institute/src/main/resources/static/pages/4-attendance/class/class-sessions.html \
      placement-assist/institute/src/main/resources/static/pages/4-attendance/class/mark-class.html \
      placement-assist/institute/src/main/resources/static/pages/4-attendance/class/class-report.html \
      placement-assist/institute/src/main/resources/static/pages/5-drive-rounds/rounds-board.html \
      placement-assist/institute/src/main/resources/static/pages/5-drive-rounds/round-form.html \
      placement-assist/institute/src/main/resources/static/pages/5-drive-rounds/round-results.html \
      placement-assist/institute/src/main/resources/static/pages/6-resume-ats/resume-upload.html \
      placement-assist/institute/src/main/resources/static/pages/6-resume-ats/resume-review.html \
      placement-assist/institute/src/main/resources/static/pages/6-resume-ats/ats-report.html \
      placement-assist/institute/src/main/resources/static/pages/7-hackathons/hackathon-list.html \
      placement-assist/institute/src/main/resources/static/pages/7-hackathons/hackathon-form.html \
      placement-assist/institute/src/main/resources/static/pages/7-hackathons/hackathon-detail.html \
      placement-assist/institute/src/main/resources/static/pages/8-internships/internship-list.html \
      placement-assist/institute/src/main/resources/static/pages/8-internships/internship-form.html \
      placement-assist/institute/src/main/resources/static/pages/8-internships/internship-detail.html \
      placement-assist/institute/src/main/resources/static/pages/9-offcampus-jobs/job-list.html \
      placement-assist/institute/src/main/resources/static/pages/9-offcampus-jobs/job-form.html \
      placement-assist/institute/src/main/resources/static/pages/9-offcampus-jobs/job-detail.html

# Documentation
touch placement-assist/docs/api-spec.md \
      placement-assist/docs/db-diagram.md \
      placement-assist/docs/deployment.md

echo "Scaffolding completed successfully!"
