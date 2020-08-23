#!/usr/bin/env bash

curl -X "POST" "http://localhost:3000/api/dashboards/db" \
  -H "Authorization: Basic YWRtaW46YWRtaW4=" \
  -H "Content-Type: application/json" \
  --data-binary @./grafana/dashboard.json
