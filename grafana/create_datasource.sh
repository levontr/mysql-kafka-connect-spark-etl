#!/usr/bin/env bash

curl -X "POST" "http://localhost:3000/api/datasources" \
  -H "Content-Type: application/json" \
  --user admin:admin \
  --data-binary @./grafana/ds.json
