#!/usr/bin/env bash
set -euo pipefail
BASE=http://localhost:8080

echo "Drop:"
curl -s "$BASE/api/v1/drops/1"; echo

echo "Create hold:"
curl -s -X POST "$BASE/api/v1/drops/1/holds"   -H 'Content-Type: application/json'   -d '{"customerId":"demo-user","quantity":2}'; echo
