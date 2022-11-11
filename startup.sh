#!/usr/bin/env sh

if [ "$APM_SERVER_ENABLED" = "true" ]; then
  echo "[INFO] App runs with apm agent"
  OPTIONS="${OPTIONS} -javaagent:/opt/connector/elastic-apm-agent-$APM_VERSION.jar
                        -Delastic.apm.service_name=${APM_SERVICE_NAME}
                        -Delastic.apm.server_urls=${APM_SERVER_URLS}
                        -Delastic.apm.application_packages=connector"
else
  echo "[WARNING] Elastic APM is not set (set these env var \$APM_SERVER_ENABLED, \$APM_SERVICE_NAME, \$APM_SERVER_URLS)"
fi

echo "The application will start..." &&
  java ${OPTIONS} -jar ./app.jar
