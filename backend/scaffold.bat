@echo off
REM ==============================================================================
REM Scaffold Script (Windows Batch) - Placement Pillar + Portal Scaffolding
REM Creates ONLY new folders and empty files without touching existing institute files.
REM ==============================================================================

echo [Scaffold] Creating directory structure for Portal and Placement pillars...

REM Data & Shared Web folders
if not exist "data\portal\uploads" mkdir "data\portal\uploads"
if not exist "data\institute\uploads\resumes" mkdir "data\institute\uploads\resumes"
if not exist "data\placement\uploads\jd" mkdir "data\placement\uploads\jd"
if not exist "data\placement\uploads\results" mkdir "data\placement\uploads\results"
if not exist "data\placement\uploads\offers" mkdir "data\placement\uploads\offers"
if not exist "data\placement\uploads\logos" mkdir "data\placement\uploads\logos"

if not exist "shared-web\css" mkdir "shared-web\css"
if not exist "shared-web\js" mkdir "shared-web\js"

REM Portal directories
if not exist "portal\src\main\java\com\placementassist\portal\config" mkdir "portal\src\main\java\com\placementassist\portal\config"
if not exist "portal\src\main\java\com\placementassist\portal\auth" mkdir "portal\src\main\java\com\placementassist\portal\auth"
if not exist "portal\src\main\java\com\placementassist\portal\status" mkdir "portal\src\main\java\com\placementassist\portal\status"
if not exist "portal\src\main\java\com\placementassist\portal\audit" mkdir "portal\src\main\java\com\placementassist\portal\audit"
if not exist "portal\src\main\resources\db\migration" mkdir "portal\src\main\resources\db\migration"
if not exist "portal\src\main\resources\static\admin" mkdir "portal\src\main\resources\static\admin"
if not exist "portal\src\main\resources\static\css" mkdir "portal\src\main\resources\static\css"
if not exist "portal\src\main\resources\static\js" mkdir "portal\src\main\resources\static\js"
if not exist "portal\src\main\resources\static\assets" mkdir "portal\src\main\resources\static\assets"
if not exist "portal\src\test\java\com\placementassist\portal" mkdir "portal\src\test\java\com\placementassist\portal"

REM Placement backend directories
if not exist "placement\src\main\java\com\placementassist\placement\config" mkdir "placement\src\main\java\com\placementassist\placement\config"
if not exist "placement\src\main\java\com\placementassist\placement\common\enums" mkdir "placement\src\main\java\com\placementassist\placement\common\enums"
if not exist "placement\src\main\java\com\placementassist\placement\institute\dto" mkdir "placement\src\main\java\com\placementassist\placement\institute\dto"
if not exist "placement\src\main\java\com\placementassist\placement\jobs" mkdir "placement\src\main\java\com\placementassist\placement\jobs"

REM Placement modules
if not exist "placement\src\main\java\com\placementassist\placement\modules\auth\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\auth\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\company\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\company\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\drive\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\drive\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\application\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\application\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\stage\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\stage\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\talk\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\talk\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\liveupdate\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\liveupdate\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\attendance\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\attendance\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\result\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\result\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\placed\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\placed\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\opportunity\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\opportunity\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\feedback\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\feedback\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\notification\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\notification\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\dashboard\dto" mkdir "placement\src\main\java\com\placementassist\placement\modules\dashboard\dto"
if not exist "placement\src\main\java\com\placementassist\placement\modules\audit" mkdir "placement\src\main\java\com\placementassist\placement\modules\audit"

REM Placement resources
if not exist "placement\src\main\resources\db\migration" mkdir "placement\src\main\resources\db\migration"
if not exist "placement\src\main\resources\stub-data" mkdir "placement\src\main\resources\stub-data"
if not exist "placement\src\main\resources\static\css" mkdir "placement\src\main\resources\static\css"
if not exist "placement\src\main\resources\static\js" mkdir "placement\src\main\resources\static\js"
if not exist "placement\src\main\resources\static\pages\1-drives" mkdir "placement\src\main\resources\static\pages\1-drives"
if not exist "placement\src\main\resources\static\pages\2-applications" mkdir "placement\src\main\resources\static\pages\2-applications"
if not exist "placement\src\main\resources\static\pages\3-stages" mkdir "placement\src\main\resources\static\pages\3-stages"
if not exist "placement\src\main\resources\static\pages\4-talks" mkdir "placement\src\main\resources\static\pages\4-talks"
if not exist "placement\src\main\resources\static\pages\5-live-updates" mkdir "placement\src\main\resources\static\pages\5-live-updates"
if not exist "placement\src\main\resources\static\pages\6-attendance" mkdir "placement\src\main\resources\static\pages\6-attendance"
if not exist "placement\src\main\resources\static\pages\7-results" mkdir "placement\src\main\resources\static\pages\7-results"
if not exist "placement\src\main\resources\static\pages\8-placed" mkdir "placement\src\main\resources\static\pages\8-placed"
if not exist "placement\src\main\resources\static\pages\9-opportunities" mkdir "placement\src\main\resources\static\pages\9-opportunities"
if not exist "placement\src\main\resources\static\pages\10-feedback" mkdir "placement\src\main\resources\static\pages\10-feedback"
if not exist "placement\src\main\resources\static\pages\11-notifications" mkdir "placement\src\main\resources\static\pages\11-notifications"
if not exist "placement\src\main\resources\static\pages\settings" mkdir "placement\src\main\resources\static\pages\settings"

REM Placement tests
if not exist "placement\src\test\java\com\placementassist\placement\contract" mkdir "placement\src\test\java\com\placementassist\placement\contract"
if not exist "placement\src\test\java\com\placementassist\placement\modules" mkdir "placement\src\test\java\com\placementassist\placement\modules"

echo [Scaffold] Directories created successfully.
