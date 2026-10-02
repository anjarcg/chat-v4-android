#!/bin/sh
# minimal wrapper shim will be replaced by gradle wrapper jar if missing - use system gradle fallback
if [ -f gradle/wrapper/gradle-wrapper.jar ]; then exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"; fi
if command -v gradle >/dev/null 2>&1; then exec gradle "$@"; else echo "gradle not found and wrapper jar missing"; exit 1; fi
