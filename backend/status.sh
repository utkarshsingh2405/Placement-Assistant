#!/usr/bin/env bash
# ==============================================================================
# Health and Status Inspector: Placement Assistance Web Application
# ==============================================================================

echo "=============================================================================="
echo "          PLACEMENT ASSISTANCE WEB APPLICATION - SERVICE STATUS"
echo "=============================================================================="
echo ""
printf "%-18s %-7s %-40s %s\n" "SERVICE" "PORT" "ENDPOINT" "STATUS"
echo "------------------------------------------------------------------------------"

check() {
    local name=$1
    local port=$2
    local url=$3
    local res
    res=$(curl -s -f "$url" 2>/dev/null || echo "")
    if [ -n "$res" ]; then
        printf "%-18s %-7s %-40s \033[0;32mONLINE\033[0m\n" "$name" "$port" "$url"
    else
        printf "%-18s %-7s %-40s \033[0;31mOFFLINE\033[0m\n" "$name" "$port" "$url"
    fi
}

check "Portal Gateway" "8080" "http://localhost:8080/actuator/health"
check "Institute Pillar" "8081" "http://localhost:8081/institute/actuator/health"
check "Placement Pillar" "8082" "http://localhost:8082/placement/actuator/health"

echo "------------------------------------------------------------------------------"
echo ""
