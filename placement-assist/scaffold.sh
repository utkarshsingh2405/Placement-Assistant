#!/usr/bin/env bash
# ==============================================================================
# Scaffold Script (Unix Shell) - Placement Pillar + Portal Scaffolding
# Creates ONLY new folders and empty files without touching existing institute files.
# ==============================================================================

set -e

echo "[Scaffold] Creating directory structure for Portal and Placement pillars..."

# Data & Shared Web folders
mkdir -p data/portal/uploads
mkdir -p data/institute/uploads/resumes
mkdir -p data/placement/uploads/{jd,results,offers,logos}

mkdir -p shared-web/{css,js}

# Portal directories
mkdir -p portal/src/main/java/com/placementassist/portal/{config,auth,status,audit}
mkdir -p portal/src/main/resources/db/migration
mkdir -p portal/src/main/resources/static/{admin,css,js,assets}
mkdir -p portal/src/test/java/com/placementassist/portal

# Placement backend directories
mkdir -p placement/src/main/java/com/placementassist/placement/{config,common/enums,institute/dto,jobs}

# Placement modules
mkdir -p placement/src/main/java/com/placementassist/placement/modules/{auth/dto,company/dto,drive/dto,application/dto,stage/dto,talk/dto,liveupdate/dto,attendance/dto,result/dto,placed/dto,opportunity/dto,feedback/dto,notification/dto,dashboard/dto,audit}

# Placement resources
mkdir -p placement/src/main/resources/db/migration
mkdir -p placement/src/main/resources/stub-data
mkdir -p placement/src/main/resources/static/{css,js}
mkdir -p placement/src/main/resources/static/pages/{1-drives,2-applications,3-stages,4-talks,5-live-updates,6-attendance,7-results,8-placed,9-opportunities,10-feedback,11-notifications,settings}

# Placement tests
mkdir -p placement/src/test/java/com/placementassist/placement/{contract,modules}

echo "[Scaffold] Directories created successfully."
